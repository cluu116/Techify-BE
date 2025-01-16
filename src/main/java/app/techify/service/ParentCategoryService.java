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
                .thumbnail(parentCategoryDto.getThumbnail())
                .isDeleted(false)
                .build();
        parentCategoryRepository.save(parentCategory);
    }

    public void updateParentCategory(Integer id, ParentCategoryDto parentCategoryDto) {
        ParentCategory parentCategory = parentCategoryRepository.findByIdAndIsDeletedFalse(id).orElseThrow();
        parentCategory.setName(parentCategoryDto.getName());
        parentCategory.setThumbnail(parentCategoryDto.getThumbnail());
        parentCategoryRepository.save(parentCategory);
    }

    public ParentCategory getParentCategoryById(Integer id) {
        return parentCategoryRepository.findByIdAndIsDeletedFalse(id).orElse(null);
    }

    public void deleteParentCategory(Integer id) {
        ParentCategory parentCategory = parentCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục cha"));
        parentCategory.setIsDeleted(true);
        parentCategoryRepository.save(parentCategory);
    }

    public List<ParentCategory> getParentCategories() {
        return parentCategoryRepository.findAllByIsDeletedFalse();
    }

    public List<CategoryResponse> getParentCategoriesWithChildren() {
        List<ParentCategory> parentCategories = parentCategoryRepository.findAllByIsDeletedFalse();
        return parentCategories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private CategoryResponse convertToResponse(ParentCategory parentCategory) {
        CategoryResponse response = new CategoryResponse();
        response.setId(parentCategory.getId());
        response.setName(parentCategory.getName());
        response.setThumbnail(parentCategory.getThumbnail());

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
