package app.techify.controller;

import app.techify.dto.PaymentMethodDto;
import app.techify.service.PaymentMethodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/payment-method")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @GetMapping("")
    public ResponseEntity<List<PaymentMethodDto>> getAllPaymentMethods() {
        return ResponseEntity.ok(paymentMethodService.getAllPaymentMethods());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentMethodDto> getPaymentMethodById(@PathVariable Short id) {
        return ResponseEntity.ok(paymentMethodService.getPaymentMethodById(id));
    }

    @PostMapping("")
    public ResponseEntity<PaymentMethodDto> createPaymentMethod(@RequestBody PaymentMethodDto paymentMethodDto) {
        return ResponseEntity.ok(paymentMethodService.createPaymentMethod(paymentMethodDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentMethodDto> updatePaymentMethod(@PathVariable Short id, @Valid @RequestBody PaymentMethodDto paymentMethodDto) {
        paymentMethodDto.setId(id);
        return ResponseEntity.ok(paymentMethodService.updatePaymentMethod(paymentMethodDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentMethod(@PathVariable Short id) {
        paymentMethodService.deletePaymentMethod(id);
        return ResponseEntity.ok().build();
    }
}