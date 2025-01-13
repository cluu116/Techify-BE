package app.techify.service;

import app.techify.dto.ProductDto;
import app.techify.entity.Product;
import app.techify.entity.ProductPromotion;
import app.techify.repository.ProductPromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductPromotionService {

    @Autowired
    private ProductPromotionRepository productPromotionRepository;

    public List<ProductDto> getActiveProductsByPromotionId(Integer promotionId) {
        List<Product> products = productPromotionRepository.findActiveProductsByPromotionId(promotionId);
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDto> getProductsNotInPromotion(Integer promotionId) {
        List<Product> products = productPromotionRepository.findActiveProductsNotInPromotion(promotionId);
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeProductFromPromotion(Integer promotionId, String productId) {
        ProductPromotion productPromotion = productPromotionRepository
                .findByPromotionIdAndProductId(promotionId, productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm trong khuyến mãi này"));

        int updatedRows = productPromotionRepository.softDeleteByPromotionIdAndProductId(promotionId, productId);
        if (updatedRows == 0) {
            throw new IllegalStateException("Không thể xóa sản phẩm khỏi khuyến mãi");
        }
    }

    private ProductDto convertToDTO(Product product) {
        ProductDto dto = new ProductDto();
        dto.setProductId(product.getId());
        dto.setCategory(product.getCategory().getId()); // Assuming Category has an getId() method
        dto.setName(product.getName());
        dto.setThumbnail(product.getThumbnail());
        dto.setBrand(product.getBrand());
        dto.setOrigin(product.getOrigin());
        dto.setUnit(product.getUnit());
        dto.setInventoryQuantity(product.getInventoryQuantity());
        dto.setAvailableQuantity(product.getAvailableQuantity());
        dto.setStatus(String.valueOf(product.getStatus()));
        dto.setSerial(product.getSerial());
        dto.setWarranty(product.getWarranty());
        dto.setBuyPrice(product.getBuyPrice());
        dto.setSellPrice(product.getSellPrice());
        dto.setTax(product.getTax());
        dto.setDescription(product.getDescription());
        dto.setColors(String.valueOf(product.getColor()));
        dto.setImages(String.valueOf(product.getImage()));
        dto.setAttributes(String.valueOf(product.getAttribute()));
        dto.setSizes(String.valueOf(product.getSize()));
        return dto;
    }
}