package dev.mmaksymko.email.configs.kafka;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class KafkaListenersManager {

    private final KafkaListenerEndpointRegistry registry;
    private final KafkaAvailabilityManager kafkaAvailabilityManager;
    private Status status;

    public KafkaListenersManager(KafkaListenerEndpointRegistry registry, KafkaAvailabilityManager kafkaAvailabilityManager) {
        this.registry = registry;
        this.kafkaAvailabilityManager = kafkaAvailabilityManager;
        status = Status.UNKNOWN;
    }

    @Scheduled(fixedRate = 15000)
    public void checkKafkaHealth() {
        boolean isKafkaAvailable = kafkaAvailabilityManager.isAvailable();
        if (isKafkaAvailable && status != Status.UP) {
            registry.start();
            status = Status.UP;
        } else if (!isKafkaAvailable && status == Status.UP) {
            registry.stop();
            status = Status.DOWN;
        }
    }
}