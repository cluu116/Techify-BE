package app.techify.service;

import app.techify.dto.ParentCategoryDto;
import app.techify.entity.Category;
import app.techify.entity.ParentCategory;
import app.techify.repository.ParentCategoryRepository;
import app.techify.dto.CategoryResponse;
import app.techify.dto.CategoryDto;
import app.techify.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParentCategoryService {
    private final ParentCategoryRepository parentCategoryRepository;
    private final CategoryRepository categoryRepository;

    public void createParentCategory(ParentCategoryDto parentCategoryDto) {
        ParentCategory parentCategory = ParentCategory.builder()
                .name(parentCategoryDto.getName())
                .build();
        parentCategoryRepository.save(parentCategory);
    }

    public void updateParentCategory(Integer id, ParentCategoryDto parentCategoryDto) {
        ParentCategory parentCategory = parentCategoryRepository.findById(id).orElseThrow();
        parentCategory.setName(parentCategoryDto.getName());
        parentCategoryRepository.save(parentCategory);
    }

    public void deleteParentCategory(Integer id) {
        parentCategoryRepository.deleteById(id);
    }

    public List<ParentCategory> getParentCategories() {
        return parentCategoryRepository.findAll();
    }

    public List<CategoryResponse> getParentCategoriesWithChildren() {
        List<ParentCategory> parentCategories = parentCategoryRepository.findAll();
        return parentCategories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private CategoryResponse convertToResponse(ParentCategory parentCategory) {
        CategoryResponse response = new CategoryResponse();
        response.setId(parentCategory.getId());
        response.setName(parentCategory.getName());
        
        List<Category> childCategories = categoryRepository.findByParentCategoryId(parentCategory.getId());
        List<CategoryDto> childDtos = childCategories.stream()
                .map(category -> {
                    CategoryDto dto = new CategoryDto();
                    dto.setId(category.getId());
                    dto.setName(category.getName());
                    dto.setParentCategory(category.getParentCategory().getId());
                    return dto;
                })
                .collect(Collectors.toList());
                
        response.setChildren(childDtos);
        return response;
    }
}
