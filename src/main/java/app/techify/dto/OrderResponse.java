package app.techify.dto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class OrderResponse {
    private String id;
    private String customerId;
    private String staffId;
    private Short paymentMethodId;
    private String transportVendorId;
    private String voucherId;
    private String customerName;
    private String staffName;
    private String paymentMethodName;
    private String transportVendorName;
    private String shippingAddress;
    private Short status;
    private String createdAt;
    private String updatedAt;
    private BigDecimal disCountValue;
    private BigDecimal shipPrice;
    private BigDecimal total;
}
