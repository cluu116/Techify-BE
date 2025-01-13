package app.techify.service;

import app.techify.dto.VoucherValidationResult;
import app.techify.entity.Voucher;
import app.techify.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepository;

    public List<Voucher> getAllVouchers() {
        return voucherRepository.findAllByIsDeletedFalse();
    }

    public Voucher getVoucherById(String id) {
        return voucherRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Voucher not found with id: " + id));
    }

    public Voucher createVoucher(Voucher voucher) {
        validateVoucherDates(voucher);
        validateDiscountValues(voucher);
        return voucherRepository.save(voucher);
    }

    public Voucher updateVoucher(Voucher voucher) {
        Voucher existingVoucher = voucherRepository.findByIdAndIsDeletedFalse(voucher.getId())
                .orElseThrow(() -> new RuntimeException("Voucher not found with id: " + voucher.getId()));

        validateVoucherDates(voucher);
        validateDiscountValues(voucher);

        existingVoucher.setId(voucher.getId());
        existingVoucher.setStartDate(voucher.getStartDate());
        existingVoucher.setEndDate(voucher.getEndDate());
        existingVoucher.setDiscountType(voucher.getDiscountType());
        existingVoucher.setDiscountValue(voucher.getDiscountValue());
        existingVoucher.setMinOrder(voucher.getMinOrder());
        existingVoucher.setMaxDiscount(voucher.getMaxDiscount());
        existingVoucher.setUsageLimit(voucher.getUsageLimit());

        return voucherRepository.save(existingVoucher);
    }

    public void deleteVoucher(String id) {
        Voucher voucher = voucherRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Voucher not found with id: " + id));
        voucher.setIsDeleted(true);
        voucherRepository.save(voucher);
    }

    public boolean checkVoucherExists(String id) {
        return voucherRepository.existsByIdAndIsDeletedFalse(id);
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

    public String applyVoucher(String voucherId, BigDecimal orderTotal) {
        try {
            Voucher voucher = voucherRepository.findByIdAndIsDeletedFalse(voucherId)
                    .orElseThrow(() -> new RuntimeException("Voucher không tồn tại: " + voucherId));

            Instant now = Instant.now();

            if (now.isBefore(voucher.getStartDate())) {
                return "Voucher chưa có hiệu lực";
            }

            if (now.isAfter(voucher.getEndDate())) {
                return "Voucher đã hết hạn";
            }

            if (voucher.getUsageLimit() != null && voucher.getUsageLimit() <= 0) {
                return "Voucher đã hết lượt sử dụng";
            }

            if (orderTotal.compareTo(voucher.getMinOrder()) < 0) {
                return "Giá trị đơn hàng chưa đạt mức tối thiểu để sử dụng voucher";
            }

            BigDecimal discountAmount;
            if (voucher.getDiscountType()) {
                discountAmount = orderTotal.multiply(BigDecimal.valueOf(voucher.getDiscountValue() / 100));
            } else {
                discountAmount = BigDecimal.valueOf(voucher.getDiscountValue());
            }

            if (voucher.getMaxDiscount() != null && discountAmount.compareTo(voucher.getMaxDiscount()) > 0) {
                discountAmount = voucher.getMaxDiscount();
            }

            if (discountAmount.compareTo(orderTotal) > 0) {
                discountAmount = orderTotal;
            }

            return discountAmount.setScale(2, RoundingMode.HALF_UP).toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }
    public Voucher updateVoucherQuantity(String id, int quantity) {
        Voucher voucher = getVoucherById(id);
        voucher.setUsageLimit(quantity);
        return voucherRepository.save(voucher);
    }
} 