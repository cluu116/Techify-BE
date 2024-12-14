package app.techify.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetailDto {
    private String orderId;
    private String productId;
    private Integer quantity;
    private BigDecimal price;
    private String color;
    private String size;
}