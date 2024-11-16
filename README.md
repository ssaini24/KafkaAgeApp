# KafkaAgeApp Setup Instructions

## Prerequisites:

1. Install Java 8 and verify with `java -version`.
2. Install Maven and verify with `mvn -v`.
3. Download Kafka from the official website (https://kafka.apache.org/downloads), extract it
   using `tar -xvzf kafka_2.13-2.8.1.tgz`, and navigate to the directory `cd kafka_2.13-2.8.1`.

## Start ZooKeeper:

Run `bin/zookeeper-server-start.sh config/zookeeper.properties`.

## Start Kafka Broker:

Run `bin/kafka-server-start.sh config/server.properties`.

## Create Required Topics:

Run `bin/kafka-topics.sh --create --topic EVEN_TOPIC --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1`
and `bin/kafka-topics.sh --create --topic ODD_TOPIC --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1`.

## Clone and Build the Application:

Clone the repository with `git clone https://github.com/ssaini24/KafkaAgeApp.git` and navigate to the project directory using `cd KafkaAgeApp`.
Build the application using `mvn clean install`.

## Run the Application:

Start the Spring Boot application with `mvn spring-boot:run`.

## Test Kafka Setup:

To produce messages, run `bin/kafka-console-producer.sh --topic EVEN_TOPIC --bootstrap-server localhost:9092` and type
messages. To consume messages,
run `bin/kafka-console-consumer.sh --topic EVEN_TOPIC --bootstrap-server localhost:9092 --from-beginning`.

## Code Structure Overview

The project is structured into the following main directories:

1. **src/main/java/org/kafka**: This is the main source directory containing the application code.
    - **consumer**: Contains the classes responsible for consuming messages from Kafka topics.
    - **models**: Contains the data models or DTOs used within the application for processing the Kafka messages.
    - **producer**: Contains the classes responsible for producing messages to Kafka topics.
    - **utility**: Includes helper classes or utility functions that assist in common operations such as serialization
      or error handling.

2. **src/main/resources**: Contains application configuration files, such as `application.properties`, where you
   configure the Kafka broker, topics, and other settings.

3. **src/test/java/org/kafka**: This directory contains the test classes for the application.
    - **consumer**: Contains test classes related to the consumer logic.
    - **producer**: Contains test classes for the producer logic.
    - **utility**: Includes test classes for utility functions.

4. **target**: This directory is generated during the build process and contains compiled class files and other build
   artifacts. It includes:
    - **classes**: The compiled application classes.
    - **generated-sources**: Contains any generated source code.
    - **generated-test-sources**: Contains any generated test source code.
    - **test-classes**: Contains compiled test classes.

## Instructions for Testing

1. **Unit Testing with JUnit**:
    - The project uses JUnit 4 for unit testing.
    - Test classes are located in the `src/test/java/org/kafka` directory, organized by component (e.g., consumer,
      producer, utility).
    - To run the tests, you can use the following Maven command:
      ```bash
      mvn test
      ```
      This will run all the tests in the project.

2. **Test Kafka Producer and Consumer**:
    - The producer and consumer classes are tested to ensure they send and receive messages to/from the correct Kafka
      topics (`EVEN_TOPIC` and `ODD_TOPIC`).
    - For unit testing, mocks are used for Kafka components (e.g., `KafkaTemplate` and `ObjectMapper`).
    - Test methods include:
        - **Producer tests**: Verify if the messages are sent to the appropriate Kafka topics based on conditions like
          age (even or odd).
        - **Error handling tests**: Ensure proper error handling, such as handling serialization errors or Kafka send
          errors.

## Assumptions or Limitations

1. **Stream Messages Not Handled**:
    - Currently, the application does not handle stream messages. It is designed to handle one message at a time (
      synchronous processing). If the application needs to be extended to support stream processing (for example,
      handling a continuous stream of data or messages), additional changes will be required in both the producer and
      consumer logic, potentially incorporating frameworks like Apache Kafka Streams or Apache Flink.

2. **Error Handling for Kafka Producer**:
    - While the Kafka producer handles errors like serialization errors and Kafka send failures, it does not include
      retries for transient Kafka errors (e.g., network or temporary broker unavailability). Implementing retry logic
      could improve robustness in case of intermittent failures.

3. **No Support for Schema Validation**:
    - The application does not currently include schema validation for the Kafka messages. If your Kafka topics require
      specific message formats (e.g., Avro or JSON schema), the application would need additional logic for validating
      the schema before publishing or consuming messages.

4. **Limited Testing for Kafka Consumer**:
    - The tests for the Kafka consumer are mocked. No actual Kafka consumer logic is tested in a real environment with
      an active Kafka cluster. This is mainly for unit testing purposes. To perform integration testing with a real
      Kafka cluster, Kafka must be properly set up and running, and the test environment must connect to it.

5. **Assumes Single-Node Kafka Setup**:
    - The current setup assumes a single-node Kafka instance. If you plan to scale the application to handle a
      multi-node Kafka cluster, additional configurations for replication, partitioning, and fault tolerance will be
      necessary.

6. **Basic Logging**:
    - The application uses basic logging, but there is no centralized logging or advanced monitoring in place. For
      production use, implementing more sophisticated logging and monitoring, such as integration with tools like ELK
      Stack (Elasticsearch, Logstash, Kibana) or Prometheus, would be beneficial.
