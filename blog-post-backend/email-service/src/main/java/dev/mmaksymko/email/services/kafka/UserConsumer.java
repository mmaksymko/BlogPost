package dev.mmaksymko.email.services.kafka;

import dev.mmaksymko.email.dto.User;
import dev.mmaksymko.email.services.EmailService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class UserConsumer {
    private final EmailService emailService;

    @KafkaListener(id = "email-user-event-listener", topics = "user-created-event", autoStartup = "false")
    public void postEventListener(@Payload User user) throws MessagingException {
        emailService.sendRegistrationEmail(user.email(), Map.of("name", user.firstName()));
    }
}

