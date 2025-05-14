package com.example.test.demo;

import com.logging.framework.LoggerFactory;
import com.logging.framework.specialized.KafkaLogger;
import org.apache.kafka.clients.consumer.*;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaLogger{
    private static final KafkaLogger logger = LoggerFactory.getKafkaLogger(KafkaConsumerService.class);

    public static void main(String[] args) {
        // Initialize logging framework
        initializeLogging();

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "test-group");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        try (Consumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList("test-topic"));

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    logger.logMessageReceived(record)
                            .with("processing_start", System.currentTimeMillis())
                            .info("Processing message");

                    // Process message
                    processMessage(record.value());

                    logger.with("processing_end", System.currentTimeMillis())
                            .info("Message processed");
                }
                consumer.commitSync();
                logger.logConsumerCommit(true);
            }
        } catch (Exception e) {
            logger.error("Kafka consumer failed", e);
        }
    }

    private static void processMessage(String message) {
        // Message processing logic
    }

    private static void initializeLogging() {
        com.logging.framework.context.ApplicationContext context =
                com.logging.framework.context.ApplicationContext.getInstance();
        context.setApplicationName("KafkaConsumerService");
        context.setEnvironment("development");

        new com.logging.framework.config.LoggingConfiguration()
                .withConsoleOutput(true)
                .includeStackTrace(true);
    }
}