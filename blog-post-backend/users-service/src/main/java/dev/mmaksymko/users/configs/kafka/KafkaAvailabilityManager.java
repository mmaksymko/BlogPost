package dev.mmaksymko.users.configs.kafka;


import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.Node;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.*;

@Configuration
public class KafkaAvailabilityManager {
    private final Properties properties;

    public KafkaAvailabilityManager(KafkaAdmin kafkaAdmin) {
        properties = new Properties();
        properties.putAll(kafkaAdmin.getConfigurationProperties());
    }

    public AdminClient getAdminClient() throws KafkaException {
        FutureTask<AdminClient> futureTask = new FutureTask<>(() -> AdminClient.create(properties));
        try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
            executorService.execute(futureTask);
            AdminClient adminClient = futureTask.get(1, TimeUnit.SECONDS);
            if (adminClient == null) {
                throw new KafkaException();
            }
            return adminClient;
        } catch (TimeoutException e) {
            futureTask.cancel(true);
            throw new KafkaException();
        } catch (Exception e) {
            throw new KafkaException();
        }
    }

    public boolean isAvailable() {
        try {
            AdminClient admin = getAdminClient();
            Collection<Node> nodes = admin.describeCluster().nodes().get();
            return nodes != null && !nodes.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}