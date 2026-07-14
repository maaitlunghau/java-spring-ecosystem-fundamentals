package com.maaitlunghau.__fullstack_user_management.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Gửi email chứa link verify / reset password.
 *
 * Link chứa TOKEN GỐC (opaque) — server chỉ lưu SHA-256 của nó. Người dùng bấm link,
 * FE lấy token từ query rồi gọi API; server hash lại để tra cứu.
 *
 * Dev: trỏ vào MailHog (localhost:1025) — email bị bắt lại, xem ở http://localhost:8025,
 * không gửi ra ngoài thật.
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String from;
    private final String frontendUrl;

    public EmailService(JavaMailSender mailSender,
                        @Value("${app.mail.from}") String from,
                        @Value("${app.frontend-url}") String frontendUrl) {
        this.mailSender = mailSender;
        this.from = from;
        this.frontendUrl = frontendUrl;
    }

    public void sendVerificationEmail(String to, String token) {
        String link = frontendUrl + "/verify-email?token=" + token;
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject("Xác thực email của bạn");
        msg.setText("Chào mừng! Nhấn vào link để kích hoạt tài khoản:\n" + link
            + "\n\nLink hết hạn sau 24 giờ.");
        mailSender.send(msg);
    }

    public void sendPasswordResetEmail(String to, String token) {
        String link = frontendUrl + "/reset-password?token=" + token;
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject("Đặt lại mật khẩu");
        msg.setText("Nhấn vào link để đặt lại mật khẩu:\n" + link
            + "\n\nLink hết hạn sau 1 giờ. Bỏ qua nếu bạn không yêu cầu.");
        mailSender.send(msg);
    }
}
