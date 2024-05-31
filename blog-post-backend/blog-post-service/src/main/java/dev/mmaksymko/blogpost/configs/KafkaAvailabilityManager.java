package dev.mmaksymko.blogpost.configs;

import org.apache.kafka.clients.admin.AdminClient;
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

    public boolean isAvailable() {
        Callable<AdminClient> task = () -> AdminClient.create(properties);
        FutureTask<AdminClient> futureTask = new FutureTask<>(task);
        AdminClient adminClient = null;
        try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
            executorService.execute(futureTask);
            adminClient = futureTask.get(1, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            futureTask.cancel(true);
        } catch (Exception e) {
            return false;
        }
        if (adminClient == null) {
            return false;
        }
        try {
            Collection<Node> nodes = adminClient.describeCluster().nodes().get();
            return nodes != null && !nodes.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}