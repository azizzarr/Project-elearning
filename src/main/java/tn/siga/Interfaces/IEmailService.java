package tn.siga.Interfaces;

public interface IEmailService {



    void sendPasswordResetEmail(String email);

    void resetPassword(String token, String newPassword);
}
