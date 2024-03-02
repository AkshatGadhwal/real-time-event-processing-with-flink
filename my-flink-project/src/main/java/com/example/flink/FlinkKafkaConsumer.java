package com.example.flink;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FlinkKafkaConsumer {

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers("localhost:9092")
                .setTopics("user-events")
                .setGroupId("flink-group")
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStream<String> kafkaStream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");

        DataStream<UserEvent> eventsStream = kafkaStream
                .map(value -> new ObjectMapper().readValue(value, UserEvent.class));

        DataStream<Tuple3<String, Integer, Integer>> aggregatedStream = eventsStream
                .keyBy(event -> event.userId)
                .window(TumblingEventTimeWindows.of(Time.minutes(5)))
                .aggregate(new AggregateFunction<UserEvent, Tuple3<String, Integer, Integer>, Tuple3<String, Integer, Integer>>() {
                    @Override
                    public Tuple3<String, Integer, Integer> createAccumulator() {
                        return new Tuple3<>("", 0, 0);
                    }

                    @Override
                    public Tuple3<String, Integer, Integer> add(UserEvent value, Tuple3<String, Integer, Integer> accumulator) {
                        accumulator.f0 = value.userId;
                        accumulator.f1 += 1; // Increment session count
                        accumulator.f2 += value.sessionDuration; // Sum session durations
                        return accumulator;
                    }

                    @Override
                    public Tuple3<String, Integer, Integer> getResult(Tuple3<String, Integer, Integer> accumulator) {
                        return accumulator;
                    }

                    @Override
                    public Tuple3<String, Integer, Integer> merge(Tuple3<String, Integer, Integer> a, Tuple3<String, Integer, Integer> b) {
                        return new Tuple3<>(a.f0, a.f1 + b.f1, a.f2 + b.f2);
                    }
                });

        // JDBC Sink to write the results into the database
        SinkFunction<Tuple3<String, Integer, Integer>> jdbcSink = JdbcSink.sink(
                "INSERT INTO user_interaction (user_id, sessions_cnt, total_time_spent) VALUES (?, ?, ?) ON CONFLICT (user_id) DO UPDATE SET sessions_cnt = EXCLUDED.sessions_cnt, total_time_spent = EXCLUDED.total_time_spent",
                (statement, tuple) -> {
                    statement.setString(1, tuple.f0);
                    statement.setInt(2, tuple.f1);
                    statement.setInt(3, tuple.f2);
                },
                new JdbcExecutionOptions.Builder().withBatchSize(100).withBatchIntervalMs(200).withMaxRetries(5).build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl("jdbc:postgresql://localhost:5432/test-db-1")
                        .withDriverName("org.postgresql.Driver")
                        .withUsername("postgres")
                        .withPassword("postgres")
                        .build()
        );

        aggregatedStream.addSink(jdbcSink).name("test-db-1 database sink");

        env.execute("Flink Kafka Consumer Job");
    }
}
