package app.techify.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDto {

    private String productId;
    private Integer category;
    private String name;
    private String thumbnail;
    private String brand;
    private String origin;
    private String unit;
    private Integer inventoryQuantity;
    private Integer availableQuantity;
    private String status;
    private String serial;
    private Integer warranty;
    private BigDecimal buyPrice;
    private BigDecimal sellPrice;
    private Double tax;
    private String description;
    private String colors;
    private String images;
    private String attributes;
    private String sizes;
}
