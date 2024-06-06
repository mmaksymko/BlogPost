package dev.mmaksymko.email.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
@AllArgsConstructor
public class EmailService {
    private JavaMailSender mailSender;
    private TemplateEngine templateEngine;

    public void sendRegistrationEmail(String to, Map<String, Object> templateModel) throws
            MessagingException {

        Context context = new Context();
        context.setVariables(templateModel);

        String htmlContent = templateEngine.process("registration", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject("Welcome to BlogPost!");
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}
