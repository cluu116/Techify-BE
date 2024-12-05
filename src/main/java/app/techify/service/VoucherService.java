package app.techify.service;

import app.techify.entity.Voucher;
import app.techify.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepository;

    public List<Voucher> getAllVouchers() {
        return voucherRepository.findAll();
    }

    public Voucher getVoucherById(String id) {
        return voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher not found with id: " + id));
    }

    public Voucher createVoucher(Voucher voucher) {
        validateVoucherDates(voucher);
        validateDiscountValues(voucher);
        return voucherRepository.save(voucher);
    }

    public Voucher updateVoucher(Voucher voucher) {
        // Verify voucher exists
        if (!voucherRepository.existsById(voucher.getId())) {
            throw new RuntimeException("Voucher not found with id: " + voucher.getId());
        }
        
        validateVoucherDates(voucher);
        validateDiscountValues(voucher);
        return voucherRepository.save(voucher);
    }

    public void deleteVoucher(String id) {
        if (!voucherRepository.existsById(id)) {
            throw new RuntimeException("Voucher not found with id: " + id);
        }
        voucherRepository.deleteById(id);
    }

    private void validateVoucherDates(Voucher voucher) {
        Instant now = Instant.now();
        
        if (voucher.getStartDate().isBefore(now)) {
            throw new RuntimeException("Start date cannot be in the past");
        }
        
        if (voucher.getEndDate().isBefore(voucher.getStartDate())) {
            throw new RuntimeException("End date must be after start date");
        }
    }

    private void validateDiscountValues(Voucher voucher) {
        if (voucher.getDiscountValue() <= 0) {
            throw new RuntimeException("Discount value must be greater than 0");
        }

        if (voucher.getDiscountType()) { // Percentage discount
            if (voucher.getDiscountValue() > 100) {
                throw new RuntimeException("Percentage discount cannot be greater than 100%");
            }
        }

        if (voucher.getMinOrder().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Minimum order amount must be greater than 0");
        }
    }
} 