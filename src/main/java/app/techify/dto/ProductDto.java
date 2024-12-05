package app.techify.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDto {

    @NotBlank(message = "Ma san pham khong duoc de trong")
    @NotNull(message = "Ma san pham khong duoc de trong")
    private String productId;
    @NotNull(message = "Khong duoc de trong danh muc san pham")
    private Integer category;
    @Size(max = 50)
    @NotBlank(message = "Ten khong duoc de trong")
    private String name;
    private String thumbnail;
    @Size(max = 50, message = "Ten phai nho hon 50 ky tu")
    private String brand;
    @Size(max = 50, message = "Ten phai nho hon 50 ky tu")
    @NotBlank(message = "Ten khong duoc de trong")
    private String origin;
    @Size(max = 20, message = "Don vi tinh phai nho hon 20 ky tu")
    @NotBlank(message = "Don vi tinh khong duoc de trong")
    private String unit;
    @Size(max = 50, message = "Serial phai nho hon 50 ky tu")
    private String serial;
    private Integer warranty;
    @NotNull(message = "Gia mua khong duoc de trong")
    private BigDecimal buyPrice;
    @NotNull(message = "Gia ban khong duoc de trong")
    private BigDecimal sellPrice;
    @NotNull(message = "Thue khong duoc de trong")
    private Double tax;
    private String description;
    private String colors;
    private String images;
    private String attributes;
}
