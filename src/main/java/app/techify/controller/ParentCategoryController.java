package app.techify.controller;

import app.techify.dto.ParentCategoryDto;
import app.techify.entity.ParentCategory;
import app.techify.service.ParentCategoryService;
import app.techify.dto.CategoryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/parent_category")
@RequiredArgsConstructor
public class ParentCategoryController {
    private final ParentCategoryService parentCategoryService;

    @GetMapping("")
    public ResponseEntity<List<ParentCategory>> getParentCategories() {
        return ResponseEntity.ok(parentCategoryService.getParentCategories());
    }

    @PostMapping("")
    public ResponseEntity<String> createParentCategory(@Valid @ModelAttribute ParentCategoryDto parentCategoryDto) {
        parentCategoryService.createParentCategory(parentCategoryDto);
        return ResponseEntity.ok("Created");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateParentCategory(@PathVariable Integer id, @Valid @RequestBody ParentCategoryDto parentCategoryDto) {
        parentCategoryService.updateParentCategory(id, parentCategoryDto);
        return ResponseEntity.ok("Updated");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteParentCategory(@PathVariable Integer id) {
        parentCategoryService.deleteParentCategory(id);
        return ResponseEntity.ok("Deleted");
    }

    @GetMapping("/with-children")
    public ResponseEntity<List<CategoryResponse>> getParentCategoriesWithChildren() {
        return ResponseEntity.ok(parentCategoryService.getParentCategoriesWithChildren());
    }
}