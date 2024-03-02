
# Real-Time Event Processing with Flink

This repository contains scripts for real-time event processing using Apache Flink. The setup involves a producer script that generates random data and pushes it to a Kafka topic named "user-events". This Kafka topic serves as a source for the Flink job.

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

1. **Database Setup**:
    - Use the provided dump file to create the required table in the PostgreSQL database "user_interaction".

2. **Running the Producer**:
    - start zookeeper and kafka, create topic
    - Execute the producer script to generate random data and push it to the Kafka topic "user-events".

3. **Running the Flink Job**:
    - start the cluster
    - create the fatjar and submit the job
    - Deploy and run the Flink job to process data from the Kafka topic and store results in the database.

4. **Verification** (Optional):
    - Use the consumer script to read from the Kafka topic "user-events" and verify data consumption.

## Files:

- **producer.py**: Script to generate random data and push it to the Kafka topic.
- **flink_job.jar**: Apache Flink job for real-time event processing.
- **consumer.py**: Script to read from the Kafka topic for verification.
- **dump.sql**: SQL dump file to create the required table in the PostgreSQL database.

## Usage:

1. Clone the repository:

   ```
   git clone https://github.com/AkshatGadhwal/real-time-event-processing-with-flink.git
   ```

2. Follow the setup instructions provided above.

## Contributors:

- [Akshat Gadhwal](https://github.com/AkshatGadhwal)

