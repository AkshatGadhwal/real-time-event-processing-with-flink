package com.example.flink;

import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.serialization.AbstractDeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;

import java.io.IOException;
import java.time.Duration;

// Create a DeserializationSchema that deserializes a byte[] into a UserEvent
class UserEventDeserializationSchema extends AbstractDeserializationSchema<UserEvent> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public UserEvent deserialize(byte[] message) throws IOException {
        try {
            return objectMapper.readValue(message, UserEvent.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null; 
        }
    }
}

public class FlinkKafkaConsumer {

    public static void main(String[] args) throws Exception {

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //Kafka source with the custom deserialization schema
        KafkaSource<UserEvent> source = KafkaSource.<UserEvent>builder()
                .setBootstrapServers("localhost:9092")
                .setTopics("user-events")
                .setGroupId("flink-group")
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new UserEventDeserializationSchema())
                .build();

        WatermarkStrategy<UserEvent> watermarkStrategy = WatermarkStrategy
                .<UserEvent>forBoundedOutOfOrderness(Duration.ofSeconds(1))
                .withTimestampAssigner(new SerializableTimestampAssigner<UserEvent>() {
                    @Override
                    public long extractTimestamp(UserEvent event, long recordTimestamp) {
                        return event.getTimestamp();
                    }
                });

        // Kafka stream
        DataStream<UserEvent> kafkaStream = env.fromSource(source, watermarkStrategy, "Kafka Source");

        DataStream<Tuple3<String, Integer, Integer>> eventStream = kafkaStream.map((value) -> new Tuple3<>(value.userId, 1, value.sessionDuration))
                .returns(TypeInformation.of(new TypeHint<Tuple3<String, Integer, Integer>>() {}));

        DataStream<Tuple3<String, Integer, Integer>> aggregatedStream = eventStream.keyBy(value -> value.f0)
                .reduce(new ReduceFunction<Tuple3<String, Integer, Integer>>() {
                    @Override
                    public Tuple3<String, Integer, Integer> reduce(Tuple3<String, Integer, Integer> value1, Tuple3<String, Integer, Integer> value2) throws Exception {
                        return new Tuple3<>(value1.f0, value1.f1 + value2.f1, value1.f2 + value2.f2);
                    }
                }).name("UserEvent Reduce");

        SinkFunction<Tuple3<String, Integer, Integer>> jdbcSink = JdbcSink.sink(
                "INSERT INTO user_interaction (user_id, sessions_cnt, total_time_spent) VALUES (?, ?, ?) ON CONFLICT (user_id) DO UPDATE SET sessions_cnt = EXCLUDED.sessions_cnt, total_time_spent = EXCLUDED.total_time_spent",
                (statement, tuple) -> {
                    statement.setString(1, tuple.f0);
                    statement.setInt(2, tuple.f1);
                    statement.setInt(3, tuple.f2);
                },
                new JdbcExecutionOptions.Builder().withBatchSize(20).withBatchIntervalMs(10000).withMaxRetries(5)
                        .build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl("jdbc:postgresql://localhost:5432/test-db-1")
                        .withDriverName("org.postgresql.Driver")
                        .withUsername("postgres")
                        .withPassword("postgres")
                        .build());

        aggregatedStream.addSink(jdbcSink).name("test-db-1 database sink");

        env.execute("Flink Kafka Consumer Job");
    }

    protected static void print(String string) {
        System.out.println(string);
    }
}
