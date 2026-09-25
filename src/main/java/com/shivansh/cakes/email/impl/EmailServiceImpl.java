package com.shivansh.cakes.email.impl;

import com.shivansh.cakes.email.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public EmailServiceImpl(
            JavaMailSender mailSender,
            @Value("${spring.mail.username}") String fromAddress
    ) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    @Override
    public void sendVerificationEmail(String toEmail, String userName, String verifyUrl) {
        String subject = "Verify your Cakes & Bakes account";
        String html = """
                <div style="font-family:Arial,sans-serif;max-width:600px;margin:auto">
                  <h2 style="color:#c0392b">Welcome to Cakes &amp; Bakes, %s!</h2>
                  <p>Please verify your email address by clicking the link below:</p>
                  <a href="%s"
                     style="display:inline-block;padding:12px 24px;background:#c0392b;color:#fff;border-radius:4px;text-decoration:none">
                    Verify Email
                  </a>
                  <p style="color:#888;font-size:12px;margin-top:24px">
                    This link expires in <strong>2 hours</strong>.
                    If you did not register, you can safely ignore this email.
                  </p>
                </div>
                """.formatted(userName, verifyUrl);
        sendHtml(toEmail, subject, html);
    }

    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        String subject = "Your Cakes & Bakes password reset OTP";
        String html = """
                <div style="font-family:Arial,sans-serif;max-width:600px;margin:auto">
                  <h2 style="color:#c0392b">Password Reset OTP</h2>
                  <p>Your one-time password is:</p>
                  <div style="font-size:36px;font-weight:bold;letter-spacing:8px;color:#c0392b;margin:16px 0">
                    %s
                  </div>
                  <p style="color:#888;font-size:12px">
                    This OTP is valid for <strong>3 minutes</strong>.
                    Do not share it with anyone.
                  </p>
                </div>
                """.formatted(otp);
        sendHtml(toEmail, subject, html);
    }

    private void sendHtml(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email to " + to + ": " + e.getMessage(), e);
        }
    }
}
