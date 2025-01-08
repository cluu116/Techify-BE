package app.techify.dto;

import app.techify.entity.Voucher;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoucherValidationResult {
    private Voucher voucher;
    private BigDecimal discountAmount;
}