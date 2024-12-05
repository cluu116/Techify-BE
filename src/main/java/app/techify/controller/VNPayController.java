package app.techify.controller;

import app.techify.dto.PaymentRequest;
import app.techify.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> paymentResult(@RequestParam Map<String, String> queryParams) {
        if (vnPayService.verifyPaymentResult(queryParams)) {
            String vnp_ResponseCode = queryParams.get("vnp_ResponseCode");
            if ("00".equals(vnp_ResponseCode)) {
                // Payment successful
                return ResponseEntity.ok("Payment successful");
            } else {
                // Payment failed
                return ResponseEntity.badRequest().body("Payment failed");
            }
        } else {
            return ResponseEntity.badRequest().body("Invalid signature");
        }
    }
}