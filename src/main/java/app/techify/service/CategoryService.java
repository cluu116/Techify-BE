package app.techify.service;

import app.techify.dto.CategoryDto;
import app.techify.entity.Category;
import app.techify.entity.ParentCategory;
import app.techify.repository.CategoryRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public void createCategory(CategoryDto categoryDto) {
        Category category = Category.builder()
                .name(categoryDto.getName())
                .parentCategory(ParentCategory.builder().id(categoryDto.getParentCategory()).build())
                .isDeleted(false)
                .build();
        categoryRepository.save(category);
    }

    public void updateCategory(Integer id, @Valid CategoryDto categoryDto) {
        Category category = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));
        category.setName(categoryDto.getName());
        category.setParentCategory(ParentCategory.builder().id(categoryDto.getParentCategory()).build());
        categoryRepository.save(category);
    }

    public void deleteCategory(Integer id) {
        Category category = categoryRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));
        category.setIsDeleted(true);
        categoryRepository.save(category);
    }

    public List<Category> getCategories() {
        return categoryRepository.findAllByIsDeletedFalse();
    }

    public Category getCategoryById(Integer id) {
        return categoryRepository.findByIdAndIsDeletedFalse(id).orElse(null);
    }
}
