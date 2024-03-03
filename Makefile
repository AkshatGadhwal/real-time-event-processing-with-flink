.PHONY: start-zookeeper start-kafka start-producer setup-postgres build-fatjar submit-flink-job

# Kafka and Zookeeper configurations
KAFKA_DIR=/path/to/kafka_2.13-3.7.0
FLINK_DIR=/path/to/flink-1.18.1
POSTGRES_CONTAINER_NAME=postgres-db
PRODUCER_SCRIPT_PATH=/path/to/producer.py
FLINK_PROJECT_DIR=/path/to/real-time-event-processing-with-flink/my-flink-project
JAR_NAME=my-flink-job.jar

# Start Zookeeper
start-zookeeper:
	${KAFKA_DIR}/bin/zookeeper-server-start.sh ${KAFKA_DIR}/config/zookeeper.properties

# Start Kafka
start-kafka:
	${KAFKA_DIR}/bin/kafka-server-start.sh ${KAFKA_DIR}/config/server.properties

# Run producer script
start-producer:
	python3 ${PRODUCER_SCRIPT_PATH}

# Setup Postgres database
setup-postgres:
	docker run --name ${POSTGRES_CONTAINER_NAME} -e POSTGRES_USER=user -e POSTGRES_PASSWORD=password -e POSTGRES_DB=test-db-1 -p 5432:5432 -d postgres:13
	sleep 10 # wait for Postgres to fully start
	docker exec -it ${POSTGRES_CONTAINER_NAME} psql -U user -d test-db-1 -a -f /path/to/db_setup.sql

# Build the fatjar
build-fatjar:
	cd ${FLINK_PROJECT_DIR} && mvn clean package

# Submit the Flink job
submit-flink-job:
	${FLINK_DIR}/bin/flink run ${FLINK_PROJECT_DIR}/target/${JAR_NAME}

# Complete setup and start everything
all: start-zookeeper start-kafka setup-postgres build-fatjar submit-flink-job start-producer
