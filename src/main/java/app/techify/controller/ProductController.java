package app.techify.controller;

import app.techify.dto.GetProductDto;
import app.techify.dto.ProductDto;
import app.techify.entity.Product;
import app.techify.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("")
    public ResponseEntity<String> createProduct(@Valid @ModelAttribute ProductDto productDto) {
        productService.createProduct(productDto);
        return ResponseEntity.ok("Created");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable String id, @Valid @RequestBody Product product) {
        try {
            productService.updateProduct(id, product);
            return ResponseEntity.ok("Updated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product not found with id: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating product: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable String id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("Deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Khong the xoa san pham nay");
        }
    }

    @GetMapping("")
    public ResponseEntity<List<GetProductDto>> getAllProducts() {
        List<GetProductDto> products = productService.getAllProductsWithDetails();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetProductDto> getProductById(@PathVariable String id) {
        try {
            GetProductDto product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<GetProductDto>> getProductsByCategory(
            @PathVariable Integer categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) String brands,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        try {
            List<String> brandList = brands != null ? Arrays.asList(brands.split(",")) : null;
            Page<GetProductDto> products = productService.getProductsByCategory(categoryId, page, size, brandList, minPrice, maxPrice);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/on-sale")
    public ResponseEntity<List<GetProductDto>> getProductsOnSale() {
        return ResponseEntity.ok(productService.getProductsOnSale());
    }

    @GetMapping("/newest")
    public ResponseEntity<List<GetProductDto>> getNewestProducts() {
        return ResponseEntity.ok(productService.getNewestProducts());
    }

    @GetMapping("/top-selling")
    public ResponseEntity<List<GetProductDto>> getTopSellingProducts() {
        return ResponseEntity.ok(productService.getTopSellingProducts(4));
    }

    @GetMapping("/brands/{categoryId}")
    public ResponseEntity<List<String>> getBrandsByCategory(@PathVariable Long categoryId) {
        List<String> brands = productService.getBrandsByCategory(categoryId);
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/category/{categoryId}/all")
    public ResponseEntity<List<GetProductDto>> getAllProductsByCategory(@PathVariable Integer categoryId) {
        try {
            List<GetProductDto> products = productService.getAllProductsByCategory(categoryId);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/{id}/related")
    public ResponseEntity<List<GetProductDto>> getRelatedProducts(
            @PathVariable String id,
            @RequestParam(defaultValue = "12") int limit) {
        try {
            List<GetProductDto> relatedProducts = productService.getRelatedProducts(id, limit);
            return ResponseEntity.ok(relatedProducts);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/search")
    public ResponseEntity<Page<GetProductDto>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<GetProductDto> results = productService.searchProducts(keyword, page, size);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<GetProductDto>> filterProducts(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) List<String> brands,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<GetProductDto> results = productService.filterProducts(categoryId, brands,
                minPrice, maxPrice,
                sortBy, sortDirection, page, size);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/top-rated")
    public ResponseEntity<List<GetProductDto>> getTopRatedProducts() {
        try {
            List<GetProductDto> topRatedProducts = productService.getTopRatedProducts(4);
            return ResponseEntity.ok(topRatedProducts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
    @PutMapping("/AvailableQuantity/{id}")
    public ResponseEntity<?> updateAvailableQuantity(
            @PathVariable String id,
            @RequestParam int quantity) {
        try {
            productService.updateAvailableQuantity(id, quantity);
            return ResponseEntity.ok().body("Product quantity updated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating product quantity");
        }
    }

    @PutMapping("/InventoryQuantity/{id}")
    public ResponseEntity<?> updateInventoryQuantity(
            @PathVariable String id,
            @RequestParam int quantity) {
        try {
            productService.updateInventoryQuantity(id, quantity);
            return ResponseEntity.ok().body("Product quantity updated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating product quantity");
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateProductStatus(@PathVariable String id, @RequestParam Short status) {
        productService.updateProductStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
