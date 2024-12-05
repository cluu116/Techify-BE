package app.techify.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryDto {
    private Integer id;
    @NotNull(message = "Khong duoc de trong danh muc san pham cha")
    private Integer parentCategory;
    @Size(max = 50, min = 2, message = "Ten phai trong khoang tu 2 den 50 ky tu")
    @NotBlank(message = "Ten khong duoc de trong")
    private String name;
}
