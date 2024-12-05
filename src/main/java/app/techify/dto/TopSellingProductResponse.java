package app.techify.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TopSellingProductResponse {
    private String productId;
    private String productName;
    private String thumbnail;
    private String categoryName;
    private BigDecimal sellPrice;
    private BigDecimal promotionPrice;
    private Integer totalQuantitySold;
    private Integer numberOfOrders;
    private BigDecimal totalRevenue;
} 