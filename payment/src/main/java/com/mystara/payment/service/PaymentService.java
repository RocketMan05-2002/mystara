package com.mystara.payment.service;

import com.mystara.payment.feign.SellerServiceClient;
import com.mystara.payment.model.Payment;
import com.mystara.payment.model.PaymentItem;
import com.mystara.payment.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final SellerServiceClient sellerServiceClient;
    private final RazorpayClient razorpayClient;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    /** ----------------- Create Razorpay Order (Test Mode Safe) ----------------- **/
    public Payment createPayment(String buyerId, String cartId, List<PaymentItem> items, BigDecimal amount, String currency) {
        Payment payment = new Payment();
        payment.setBuyerId(buyerId);
        payment.setCartId(cartId);
        payment.setItems(items);
        payment.setAmount(amount);
        payment.setCurrency(currency != null ? currency : "INR");
        payment.setStatus("PENDING");
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        try {
            // Razorpay only accepts amount up to 1 crore (1,00,00,000 INR = 10^9 paise)
            BigDecimal amountInPaise = amount.multiply(BigDecimal.valueOf(100));
            if (amountInPaise.compareTo(BigDecimal.valueOf(1000000000)) > 0) {
                throw new IllegalArgumentException("Amount exceeds maximum allowed by Razorpay");
            }

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise.longValue());
            orderRequest.put("currency", "INR");

            // 🔧 Limit receipt to 40 chars max
            String receipt = "ord_" + System.currentTimeMillis();
            if (receipt.length() > 40) {
                receipt = receipt.substring(0, 40);
            }
            orderRequest.put("receipt", receipt);

            // ✅ Create order in Razorpay (Test Mode safe)
            Order order = razorpayClient.orders.create(orderRequest);

            payment.setRazorpayOrderId(order.get("id"));
            payment.setStatus("CREATED");
            payment.setUpdatedAt(LocalDateTime.now());

        } catch (RazorpayException e) {
            payment.setStatus("FAILED");
            payment.setErrorMessage("Razorpay Error: " + e.getMessage());
        } catch (Exception e) {
            payment.setStatus("FAILED");
            payment.setErrorMessage("Payment Creation Error: " + e.getMessage());
        }

        return paymentRepository.save(payment);
    }

    /** ----------------- Confirm Razorpay Payment ----------------- **/
    @Transactional
    public Payment confirmPayment(String id, String razorpayPaymentId) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));

        try {
            // Fetch payment details from Razorpay
            com.razorpay.Payment razorpayPayment = razorpayClient.payments.fetch(razorpayPaymentId);

            String status = razorpayPayment.get("status");
            log.info("🔍 Razorpay Payment {} has status: {}", razorpayPaymentId, status);

            if ("captured".equalsIgnoreCase(status)) {
                payment.setStatus("SUCCESS");
                payment.setRazorpayPaymentId(razorpayPaymentId);
                payment.setUpdatedAt(LocalDateTime.now());
                paymentRepository.save(payment);

                // ✅ Reduce product stock after successful payment
                reduceProductQuantities(payment.getItems());

                log.info("✅ Payment {} succeeded with Razorpay Payment ID {}", id, razorpayPaymentId);
            } else {
                payment.setStatus("FAILED");
                payment.setErrorMessage("Payment status: " + status);
                payment.setUpdatedAt(LocalDateTime.now());
                paymentRepository.save(payment);

                log.warn("⚠️ Payment {} failed with status {}", id, status);
            }

            return payment;

        } catch (Exception e) {
            payment.setStatus("FAILED");
            payment.setErrorMessage(e.getMessage());
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            log.error("🚨 Error confirming payment {}: {}", id, e.getMessage());
            return payment;
        }
    }

    /** ----------------- Reduce Stock After Successful Payment ----------------- **/
    private void reduceProductQuantities(List<PaymentItem> items) {
        for (PaymentItem item : items) {
            try {
                sellerServiceClient.reduceStock(item.getProductId(), item.getQuantity());
                log.info("📦 Reduced stock for product {}", item.getProductId());
            } catch (Exception e) {
                log.error("⚠️ Failed to reduce stock for product {}: {}", item.getProductId(), e.getMessage());
            }
        }
    }

    /** ----------------- Utility Methods ----------------- **/
    public List<Payment> getPaymentsByBuyer(String buyerId) {
        return paymentRepository.findByBuyerId(buyerId);
    }

    public Optional<Payment> getPaymentById(String id) {
        return paymentRepository.findById(id);
    }

    public Optional<Payment> getPaymentByRazorpayOrderId(String orderId) {
        return paymentRepository.findByRazorpayOrderId(orderId);
    }
}
