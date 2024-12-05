package app.techify.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderDetailResponse {
    private Integer id;
    private String productId;
    private String productName;
    private String productThumbnail;
    private String color;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal total;
} 