package app.techify.controller;

import app.techify.dto.PaymentRequest;
import app.techify.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Map<String, Object>> paymentResult(@RequestParam Map<String, String> queryParams) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (queryParams.isEmpty()) {
                response.put("success", false);
                response.put("message", "No payment information received");
                return ResponseEntity.badRequest().body(response);
            }

            boolean isValidSignature = vnPayService.verifyPaymentResult(queryParams);

            if (isValidSignature) {
                String vnp_ResponseCode = queryParams.get("vnp_ResponseCode");

                if ("00".equals(vnp_ResponseCode)) {
                    // Payment successful
                    response.put("success", true);
                    response.put("message", "Thanh toán thành công");
                    response.put("transactionNo", queryParams.get("vnp_TransactionNo"));
                    response.put("amount", queryParams.get("vnp_Amount"));
                    response.put("orderInfo", queryParams.get("vnp_OrderInfo"));
                    return ResponseEntity.ok(response);
                } else {
                    // Payment failed
                    response.put("success", false);
                    response.put("message", "Thanh toán thất bại");
                    response.put("responseCode", vnp_ResponseCode);
                    return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(response);
                }
            } else {
                response.put("success", false);
                response.put("message", "Invalid signature");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error processing payment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}