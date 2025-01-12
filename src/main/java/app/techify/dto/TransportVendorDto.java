package app.techify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransportVendorDto {
    private String id;
    private String name;
    private String phone;
    private String email;
    private Boolean status = false;
    private BigDecimal basePrice;
}
