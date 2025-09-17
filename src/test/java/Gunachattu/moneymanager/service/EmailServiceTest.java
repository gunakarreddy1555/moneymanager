package Gunachattu.moneymanager.service;
import Gunachattu.moneymanager.service.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;


    //hello
    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        // Use reflection to set the private field 'fromEmail'
        try {
            java.lang.reflect.Field field = EmailService.class.getDeclaredField("fromEmail");
            field.setAccessible(true);
            field.set(emailService, "noreply@example.com");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DisplayName("sendEmail sends email with correct parameters")
    @org.junit.jupiter.api.Test
    void sendEmail_SendsEmailWithCorrectParameters() {
        String to = "user@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        emailService.sendEmail(to, subject, body);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());
        SimpleMailMessage sentMessage = captor.getValue();

        assertEquals("noreply@example.com", sentMessage.getFrom());
        assertArrayEquals(new String[]{to}, sentMessage.getTo());
        assertEquals(subject, sentMessage.getSubject());
        assertEquals(body, sentMessage.getText());
    }

    @DisplayName("sendEmail throws RuntimeException when mailSender throws exception")
    @org.junit.jupiter.api.Test
    void sendEmail_ThrowsRuntimeExceptionOnMailSenderError() {
        doThrow(new RuntimeException("Mail error")).when(mailSender).send(any(SimpleMailMessage.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                emailService.sendEmail("user@example.com", "Subject", "Body")
        );
        assertEquals("Mail error", exception.getMessage());
    }

    @DisplayName("sendEmail handles null recipient gracefully")
    @org.junit.jupiter.api.Test
    void sendEmail_NullRecipient_ThrowsException() {
        assertThrows(RuntimeException.class, () ->
                emailService.sendEmail(null, "Subject", "Body")
        );
    }

    @DisplayName("sendEmail handles empty subject and body")
    @org.junit.jupiter.api.Test
    void sendEmail_EmptySubjectAndBody_SendsEmail() {
        emailService.sendEmail("user@example.com", "", "");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());
        SimpleMailMessage sentMessage = captor.getValue();

        assertEquals("", sentMessage.getSubject());
        assertEquals("", sentMessage.getText());
    }
}
