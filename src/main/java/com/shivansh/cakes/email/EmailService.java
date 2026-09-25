package com.shivansh.cakes.email;

/**
 * Email sending contract. Implementations use JavaMailSender.
 */
public interface EmailService {

    /**
     * Sends a verification email to the newly registered user.
     *
     * @param toEmail   recipient email
     * @param userName  user's name (for greeting)
     * @param verifyUrl full verification URL including userId and raw token
     */
    void sendVerificationEmail(String toEmail, String userName, String verifyUrl);

    /**
     * Sends a 6-digit OTP for password reset.
     *
     * @param toEmail recipient email
     * @param otp     the 6-digit OTP
     */
    void sendOtpEmail(String toEmail, String otp);
}
