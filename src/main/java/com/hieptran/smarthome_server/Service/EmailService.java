package com.hieptran.smarthome_server.Service;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
//import org.thymeleaf.TemplateEngine;
//import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

//    private final TemplateEngine templateEngine;

    public void sendVerificationEmail(String recipientEmail, 
                                      String recipientName, 
                                      String verificationCode, 
                                      int expirationMinutes) {

//        Context context = getContext(recipientName, verificationCode, expirationMinutes);

//        String htmlContent = templateEngine.process("email.html", context);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(recipientEmail);
            helper.setSubject("Verify Your Email");
            helper.setText("htmlContent", true); // true để gửi dưới dạng HTML
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    public void sendEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);

        mailSender.send(message);
    }
//
//    private Context getContext(String recipientName, String verificationCode, int expirationMinutes) {
//        Context context = new Context();
//        context.setVariable("logoUrl", "https://example.com/logo.png");
//        context.setVariable("recipientName", recipientName);
//        context.setVariable("verificationCode", verificationCode);
//        context.setVariable("expirationMinutes", expirationMinutes);
//        context.setVariable("verificationUrl", "https://example.com/verify?code=" + verificationCode);
//        context.setVariable("supportUrl", "https://home-pod.vercel.app/how-it-works");
//        context.setVariable("", "Thanh Xuan, Ha Noi, Viet Nam");
//        context.setVariable("settingsUrl", "https://example.com/settings");
//        return context;
//    }

}
