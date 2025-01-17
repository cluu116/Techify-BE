package app.techify.controller;

import app.techify.entity.Voucher;
import app.techify.service.VoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("api/voucher")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;

    @GetMapping("")
    public ResponseEntity<List<Voucher>> getAllVouchers() {
        return ResponseEntity.ok(voucherService.getAllVouchers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Voucher> getVoucherById(@PathVariable String id) {
        return ResponseEntity.ok(voucherService.getVoucherById(id));
    }

    @PostMapping("")
    public ResponseEntity<Voucher> createVoucher(@Valid @RequestBody Voucher voucher) {
        return ResponseEntity.ok(voucherService.createVoucher(voucher));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Voucher> updateVoucher(@PathVariable String id, @Valid @RequestBody Voucher voucher) {
        voucher.setId(id);
        return ResponseEntity.ok(voucherService.updateVoucher(voucher));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoucher(@PathVariable String id) {
        voucherService.deleteVoucher(id);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/check/{id}")
    public ResponseEntity<Boolean> checkVoucherExists(@PathVariable String id) {
        boolean exists = voucherService.checkVoucherExists(id);
        return ResponseEntity.ok(exists);
    }
    @PostMapping("/apply")
    public ResponseEntity<String> applyVoucher(@RequestParam String voucherId, @RequestParam BigDecimal orderTotal) {
        String result = voucherService.applyVoucher(voucherId, orderTotal);
        return ResponseEntity.ok(result);
    }
    @PatchMapping("/{id}/quantity")
    public ResponseEntity<Voucher> updateVoucherQuantity(@PathVariable String id, @RequestParam int quantity) {
        Voucher updatedVoucher = voucherService.updateVoucherQuantity(id, quantity);
        return ResponseEntity.ok(updatedVoucher);
    }
} 