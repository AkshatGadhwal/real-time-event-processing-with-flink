# Real-Time Event Processing with Flink

This repository contains scripts for real-time event processing using Apache Flink. The setup involves a producer script that generates random data and pushes it to a Kafka topic named "user-events". This Kafka topic serves as a source for the Flink job. The Flink job reads data from the Kafka topic, processes it in real-time, calculates the total and average time spent by a user, and stores the results in a PostgreSQL database named "test-db-1" and a table named "user_interaction".

## Technical Concepts Used:

1. **UserEvent class**:
    - The `UserEvent` class represents the structure of the event data. It contains fields such as `userId`, `timestamp`, and other relevant attributes.

2. **Custom Deserializer**:
    - A custom deserializer is used to read the data from the Kafka source in the `UserEvent` format. This deserializer is responsible for converting the binary data received from Kafka into instances of the `UserEvent` class.

3. **Custom Watermarking Strategy**:
    - A custom watermarking strategy is implemented to extract and assign the source data timestamp as the watermark. Watermarks are used in event time processing to track the progress of time and determine when to trigger time-based operations.

4. **Aggregation**:
    - The source stream is separated by `userId` to create a parallel stream. This allows for independent processing of events belonging to different users. Aggregation operations, such as calculating the total time spent and session count, are performed on each parallel stream.

5. **PostgreSQL Database Sink**:
    - The PostgreSQL database is configured as the sink for storing the processed results. The Flink job writes the aggregated data, such as the total time spent and session count, to the `user_interaction` table in the `test-db-1` database.


## Components:

1. **Producer Script**:
        - Generates random data and sends it to the Kafka topic "user-events".

2. **Flink Job**:
        - Reads data from the Kafka topic "user-events" and performs real-time processing.

3. **Database**:
        - The Flink job processes data from Kafka and stores the results in a PostgreSQL database named "user_interaction".

4. **Consumer Script** (Optional for verification):
        - Reads from the Kafka topic "user-events" and prints data to the console for verification.

## Setup Instructions:

1. **Java Setup**:
   - Install Java 8 or higher.

2. **Database Setup**:
   - Use the provided dump file to create the required table in the PostgreSQL database "test-db-1".
       - The table is named "user_interaction" and has the following schema:
           ```sql
           CREATE TABLE user_interaction (
                       user_id TEXT,
                       sessions_cnt INT,
                       total_time_spend INT
           );
           ```
       - Update the database credentials (username and password) in the Flink job.

3. **Kafka Setup**:
    - Install Kafka:
        ```shell
        wget https://downloads.apache.org/kafka/3.7.0/kafka_2.13-3.7.0.tgz
        tar -xzf kafka_2.13-3.7.0.tgz
        cd kafka_2.13-3.7.0
        ```
    - Update the Kafka configuration file "server.properties" to set the following properties:
        ```properties
        listeners=PLAINTEXT://:9092
        advertised.listeners=PLAINTEXT://localhost:9092
        ```
    - Start the Zookeeper and Kafka server using the following commands:
        ```shell
        bin/zookeeper-server-start.sh config/zookeeper.properties
        bin/kafka-server-start.sh config/server.properties
        ```
    - Create a Kafka topic named "user-events" using the following command:
        ```shell
        bin/kafka-topics.sh --create --topic user-events --bootstrap-server localhost:9092
        ```
    - Verify the topic creation using the following command:
        ```shell
        bin/kafka-topics.sh --list --bootstrap-server localhost:9092
        ```
    - Start a Kafka consumer to verify the topic:
        ```shell
        kafka-console-consumer --bootstrap-server localhost:9092 --topic user-events --latest
        ```
    - Start a Kafka producer to verify the topic:
        ```shell
        kafka-console-producer --bootstrap-server localhost:9092 --topic user-events
        ```
    - Use the producer script to generate random data and push it to the Kafka topic "user-events".

4. **Flink Setup**:
     - Install Apache Flink:
         ```shell
          wget https://downloads.apache.org/flink/flink-1.18.1/flink-1.18.1-bin-scala_2.12.tgz
          tar -xzf flink-*.tgz
          cd flink-* && ls -l
       ```
     - Start the Flink cluster using the following command:
         ```shell
         ./bin/start-cluster.sh
         ```
     - Create a fatjar for the Flink job using the following command:
         ```shell
         ./bin/flink run /path/to/fatjar
         ```
     - Verify the job submission using the following command:
         ```shell
         ./bin/flink list
         ```
     - Use the consumer script to read from the Kafka topic "user-events" and verify data consumption.

5. **Verification** (Optional):
    - Use the consumer script to read from the Kafka topic "user-events" and verify data consumption.

6. **Running the Producer**:
    - Install the required packages using the following command:
        ```shell
        pip install -r requirements.txt
        ```
    - (if not already) Start Zookeeper and Kafka, create topic.
    - Execute the producer script to generate random data and push it to the Kafka topic "user-events".

7. **Creating the Fatjar**:
    - clone the repository:
        ```shell
        git clone https://github.com/AkshatGadhwal/real-time-event-processing-with-flink.git 
        ```
    - Go to the project directory:
        ```shell
        cd real-time-event-processing-with-flink
        ```
    - Go to the Flink job directory:
        ```shell
        cd my-flink-project
        ```
    - Create a fatjar for the Flink job using the following command:
        ```shell
        mvn clean package
        ```
    - Copy the path of the fatjar and submit the Flink job.

8. **Running the Flink Job**:
    - (if not started already) Start the cluster.
    - (if not created already) Create the fatjar and submit the job.
    - Access the Flink dashboard using the following link:
        ```shell
        http://localhost:8081
        ```
    - Verify the job submission using the following command:
        ```shell
        ./bin/flink list
      ```
9. **Cleanup**:
    - Stop the Flink cluster using the following command:
        ```shell
        ./bin/stop-cluster.sh
        ```
    - Stop the Zookeeper and Kafka server using the following commands:
        ```shell
        bin/kafka-server-stop.sh
        bin/zookeeper-server-stop.sh 
      ```

## Workflow:

1. **Java Setup**:
    - Install Java 8 or higher.
    - Install Maven.

2. **Database Setup**:
    - Create the required table in the PostgreSQL database.
    - Update the database credentials in the Flink job.

3. **Kafka Setup**:
    - Install Kafka and configure it.
    - Start the Zookeeper and Kafka server.
    - Create a Kafka topic and verify its creation.

4. **Creating the Fatjar**:
    - Create a fatjar for the Flink job.
    - Submit the Flink job.

5. **Flink Setup**:
    - start the Flink cluster.
    - Submit the Flink job with the Fatjar.
    - Access the Flink dashboard to monitor the job.

6. **Running the Producer**:
    - Install the required packages.
    - Start Zookeeper and Kafka, create topic.
    - Execute the producer script.

7. **Verification** (Optional):
    - Use the consumer script to verify data consumption.

8. **Cleanup**:
    - Stop the Flink cluster.
    - Stop the Zookeeper and Kafka server.

Screenshots:
![alt text](./Screenshots/Screenshot%20from%202024-03-04%2008-55-40.png)
![alt text](./Screenshots/Screenshot%20from%202024-03-04%2008-56-46.png)
