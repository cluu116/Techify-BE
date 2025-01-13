package app.techify.dto;

import app.techify.entity.Category;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class GetProductDto {
    private String id;
    private String name;
    private Category category;
    private String thumbnail;
    private String brand;
    private String origin;
    private String unit;
    private String serial;
    private Integer warranty;
    private String description;
    private BigDecimal sellPrice;
    private BigDecimal buyPrice;
    private Double tax;
    private BigDecimal promotionPrice;
    private Integer reviewCount;
    private Double avgRating;
    private String colors;
    private String sizes;
    private String images;
    private String attributes;
    private Instant promotionEndDate;
    private String formattedDiscount;
    private Integer inventoryQuantity;
    private Integer availableQuantity;
    private String status;
    private Instant createdAt;
}
