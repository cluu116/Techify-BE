package app.techify.dto;

import lombok.Data;
import java.util.List;

@Data
public class CategoryResponse {
    private Integer id;
    private String name;
    private String thumbnail;
    private List<CategoryDto> children;
} 