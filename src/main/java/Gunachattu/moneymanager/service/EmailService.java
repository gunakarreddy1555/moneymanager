package Gunachattu.moneymanager.service;

import ch.qos.logback.core.net.SyslogOutputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;

    public void sendEmail(String to, String subject, String body) {
     try{
     SimpleMailMessage message = new SimpleMailMessage();
         System.out.println("Sending email to " + to);
         System.out.println("Sending email to " + fromEmail);
         message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

         System.out.println(message);
        mailSender.send(message);
     }catch(Exception e){
        throw new RuntimeException(e.getMessage());
     }
    }
}
