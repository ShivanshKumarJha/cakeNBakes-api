package com.shivansh.cakes.payment;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.shivansh.cakes.common.exception.BusinessException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Service
public class RazorpayService {

    private final RazorpayClient client;
    private final String keySecret;

    public RazorpayService(
            @Value("${app.payment.razorpay.key-id}") String keyId,
            @Value("${app.payment.razorpay.key-secret}") String keySecret
    ) {
        try {
            this.client = new RazorpayClient(keyId, keySecret);
            this.keySecret = keySecret;
        } catch (RazorpayException e) {
            throw new RuntimeException("Failed to initialize Razorpay Client", e);
        }
    }

    /**
     * Creates an order in Razorpay.
     * @param amount in INR
     * @return the Razorpay order ID (e.g., order_xxxx)
     */
    public String createOrder(BigDecimal amount) {
        try {
            JSONObject options = new JSONObject();
            // Razorpay expects amount in paise
            options.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue());
            options.put("currency", "INR");
            options.put("receipt", "txn_" + System.currentTimeMillis());

            Order order = client.orders.create(options);
            return order.get("id");
        } catch (RazorpayException e) {
            throw new BusinessException("Failed to create Razorpay order: " + e.getMessage());
        }
    }

    /**
     * Verifies the Razorpay payment signature.
     */
    public boolean verifySignature(String orderId, String paymentId, String signature) {
        try {
            String payload = orderId + "|" + paymentId;
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(keySecret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            byte[] hash = sha256_HMAC.doFinal(payload.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().equals(signature);
        } catch (Exception e) {
            return false;
        }
    }
}
