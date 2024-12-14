package app.techify.controller;

import app.techify.dto.PaymentRequest;
import app.techify.service.VNPayService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/vnpay")
@RequiredArgsConstructor
public class VNPayController {

    private final VNPayService vnPayService;

    @PostMapping("/create-payment")
    public ResponseEntity<String> createPayment(@RequestBody PaymentRequest paymentRequest, HttpServletRequest request) {
        String paymentUrl = vnPayService.createPaymentUrl(paymentRequest.getOrderId(), paymentRequest.getAmount(), request.getRemoteAddr());
        return ResponseEntity.ok(paymentUrl);
    }

    @GetMapping("/payment-result")
    public void paymentResult(@RequestParam Map<String, String> queryParams, HttpServletResponse response) throws IOException {
        Map<String, Object> result = new HashMap<>();
        try {
            if (queryParams.isEmpty()) {
                result.put("success", false);
                result.put("message", "No payment information received");
            } else {
                boolean isValidSignature = vnPayService.verifyPaymentResult(queryParams);

                if (isValidSignature) {
                    String vnp_ResponseCode = queryParams.get("vnp_ResponseCode");

                    if ("00".equals(vnp_ResponseCode)) {
                        // Payment successful
                        result.put("success", true);
                        result.put("message", "Thanh toán thành công");
                        result.put("transactionNo", queryParams.get("vnp_TransactionNo"));
                        result.put("amount", queryParams.get("vnp_Amount"));
                        result.put("orderInfo", queryParams.get("vnp_OrderInfo"));
                    } else {
                        // Payment failed
                        result.put("success", false);
                        result.put("message", "Thanh toán thất bại");
                        result.put("responseCode", vnp_ResponseCode);
                    }
                } else {
                    result.put("success", false);
                    result.put("message", "Invalid signature");
                }
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error processing payment: " + e.getMessage());
        }

        // Chuyển hướng về frontend với kết quả thanh toán
        String frontendUrl = "http://localhost:5173/payment-result";
        String redirectUrl = frontendUrl + "?result=" + URLEncoder.encode(new ObjectMapper().writeValueAsString(result), StandardCharsets.UTF_8);
        response.sendRedirect(redirectUrl);
    }
}