package app.techify.service;

import app.techify.entity.PaymentMethod;
import app.techify.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    public List<PaymentMethod> getAllPaymentMethods() {
        return paymentMethodRepository.findAll();
    }

    public PaymentMethod getPaymentMethodById(Short id) {
        return paymentMethodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment method not found with id: " + id));
    }

    public PaymentMethod createPaymentMethod(PaymentMethod paymentMethod) {
        validatePaymentMethod(paymentMethod);
        return paymentMethodRepository.save(paymentMethod);
    }

    public PaymentMethod updatePaymentMethod(PaymentMethod paymentMethod) {
        if (!paymentMethodRepository.existsById(paymentMethod.getId())) {
            throw new RuntimeException("Payment method not found with id: " + paymentMethod.getId());
        }
        validatePaymentMethod(paymentMethod);
        return paymentMethodRepository.save(paymentMethod);
    }

    public void deletePaymentMethod(Short id) {
        if (!paymentMethodRepository.existsById(id)) {
            throw new RuntimeException("Payment method not found with id: " + id);
        }
        paymentMethodRepository.deleteById(id);
    }

    private void validatePaymentMethod(PaymentMethod paymentMethod) {
        if (paymentMethod.getName() == null || paymentMethod.getName().trim().isEmpty()) {
            throw new RuntimeException("Payment method name cannot be empty");
        }
        if (paymentMethod.getName().length() > 50) {
            throw new RuntimeException("Payment method name cannot be longer than 50 characters");
        }
    }
} 