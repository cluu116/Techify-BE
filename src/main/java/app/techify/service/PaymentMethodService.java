package app.techify.service;

import app.techify.dto.PaymentMethodDto;
import app.techify.entity.PaymentMethod;
import app.techify.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    public List<PaymentMethodDto> getAllPaymentMethods() {
        return paymentMethodRepository.findAllByIsDeletedFalse().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public PaymentMethodDto getPaymentMethodById(Short id) {
        PaymentMethod paymentMethod = paymentMethodRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Payment method not found with id: " + id));
        return convertToDto(paymentMethod);
    }

    public PaymentMethodDto createPaymentMethod(PaymentMethodDto paymentMethodDto) {
        validatePaymentMethod(paymentMethodDto);
        PaymentMethod paymentMethod = convertToEntity(paymentMethodDto);
        paymentMethod.setIsDeleted(false);
        PaymentMethod savedPaymentMethod = paymentMethodRepository.save(paymentMethod);
        return convertToDto(savedPaymentMethod);
    }

    public PaymentMethodDto updatePaymentMethod(PaymentMethodDto paymentMethodDto) {
        PaymentMethod existingPaymentMethod = paymentMethodRepository.findById(paymentMethodDto.getId())
                .orElseThrow(() -> new RuntimeException("Payment method not found with id: " + paymentMethodDto.getId()));
        validatePaymentMethod(paymentMethodDto);
        existingPaymentMethod.setName(paymentMethodDto.getName());
        PaymentMethod updatedPaymentMethod = paymentMethodRepository.save(existingPaymentMethod);
        return convertToDto(updatedPaymentMethod);
    }

    public void deletePaymentMethod(Short id) {
        PaymentMethod paymentMethod = paymentMethodRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Payment method not found with id: " + id));
        paymentMethod.setIsDeleted(true);
        paymentMethodRepository.save(paymentMethod);
    }

    private void validatePaymentMethod(PaymentMethodDto paymentMethodDto) {
        if (paymentMethodDto.getName() == null || paymentMethodDto.getName().trim().isEmpty()) {
            throw new RuntimeException("Payment method name cannot be empty");
        }
        if (paymentMethodDto.getName().length() > 50) {
            throw new RuntimeException("Payment method name cannot be longer than 50 characters");
        }
    }

    private PaymentMethodDto convertToDto(PaymentMethod paymentMethod) {
        return new PaymentMethodDto(
                paymentMethod.getId(),
                paymentMethod.getName(),
                paymentMethod.getIsDeleted()
        );
    }

    private PaymentMethod convertToEntity(PaymentMethodDto paymentMethodDto) {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(paymentMethodDto.getId());
        paymentMethod.setName(paymentMethodDto.getName());
        paymentMethod.setIsDeleted(paymentMethodDto.getIsDeleted());
        return paymentMethod;
    }
}