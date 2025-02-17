package tn.siga.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import tn.siga.Interfaces.IEmailService;
import tn.siga.entities.User;
import tn.siga.repositories.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService {

    @Autowired
    private JavaMailSender mailSender;

    private final UserRepository userRepository;

    @Override
    public void sendPasswordResetEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token;
        boolean tokenExists;
        do {
            token = UUID.randomUUID().toString();
            tokenExists = userRepository.existsByResetToken(token);
        } while (tokenExists);

        user.setResetToken(token);
        userRepository.save(user);

        String resetLink = "http://localhost:4200/auth/new-password?token=" + token;
        sendEmail(user.getEmail(), "Reset Your Password",
                "Click the link to reset your password: " + resetLink);
}


    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
    @Override
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);
    }

}
