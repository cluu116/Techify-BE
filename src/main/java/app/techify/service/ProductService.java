package app.techify.service;

import app.techify.dto.GetProductDto;
import app.techify.dto.ProductDto;
import app.techify.entity.*;
import app.techify.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ColorRepository colorRepository;
    private final ImageRepository imageRepository;
    private final AttributeRepository attributeRepository;
    private final ObjectMapper objectMapper;
    private final ProductPromotionRepository productPromotionRepository;
    private final SizeRepository sizeRepository;

    public void createProduct(ProductDto productDto) {
        Color color = colorRepository.save(Color.builder().colorJson(productDto.getColors()).build());
        Size size = sizeRepository.save(Size.builder().sizeJson(productDto.getSizes()).build());
        Image image = imageRepository.save(Image.builder().imageJson(productDto.getImages()).build());
        Attribute attribute = attributeRepository.save(Attribute.builder().attributeJson(productDto.getAttributes()).build());

        Product.ProductBuilder productBuilder = Product.builder()
                .id(productDto.getProductId())
                .category(Category.builder().id(productDto.getCategory()).build())
                .name(productDto.getName())
                .thumbnail(productDto.getThumbnail())
                .brand(productDto.getBrand())
                .origin(productDto.getOrigin())
                .unit(productDto.getUnit())
                .serial(productDto.getSerial())
                .inventoryQuantity(productDto.getInventoryQuantity())
                .warranty(productDto.getWarranty())
                .buyPrice(productDto.getBuyPrice())
                .sellPrice(productDto.getSellPrice())
                .tax(productDto.getTax())
                .description(productDto.getDescription())
                .color(color)
                .image(image)
                .size(size)
                .attribute(attribute)
                .createdAt(Instant.now());

        switch (productDto.getStatus()) {
            case "active" -> productBuilder.status((short) 1);
            case "comingSoon" -> productBuilder.status((short) 4);
            case null, default -> productBuilder.status((short) 0);
        }

        Product product = productBuilder.build();
        productRepository.save(product);
    }

    public void updateProduct(String id, @Valid Product product) {
        Product productToUpdate = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // Update basic fields
        productToUpdate.setCategory(product.getCategory());
        productToUpdate.setName(product.getName());
        productToUpdate.setThumbnail(product.getThumbnail());
        productToUpdate.setBrand(product.getBrand());
        productToUpdate.setOrigin(product.getOrigin());
        productToUpdate.setUnit(product.getUnit());
        productToUpdate.setSerial(product.getSerial());
        productToUpdate.setWarranty(product.getWarranty());
        productToUpdate.setBuyPrice(product.getBuyPrice());
        productToUpdate.setSellPrice(product.getSellPrice());
        productToUpdate.setTax(product.getTax());
        productToUpdate.setDescription(product.getDescription());

        // Update related entities if they exist
        if (product.getColor() != null) {
            Color color = colorRepository.save(product.getColor());
            productToUpdate.setColor(color);
        }
        if (product.getImage() != null) {
            Image image = imageRepository.save(product.getImage());
            productToUpdate.setImage(image);
        }
        if (product.getAttribute() != null) {
            Attribute attribute = attributeRepository.save(product.getAttribute());
            productToUpdate.setAttribute(attribute);
        }

        productRepository.save(productToUpdate);
    }

    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }

    public List<GetProductDto> getAllProductsWithDetails() {
        List<Product> products = productRepository.findAllWithDetails();
        return products.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private GetProductDto convertToDTO(Product product) {
        GetProductDto dto = new GetProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setThumbnail(product.getThumbnail());
        dto.setCategory(product.getCategory());
        dto.setBrand(product.getBrand());
        dto.setOrigin(product.getOrigin());
        dto.setUnit(product.getUnit());
        dto.setSellPrice(product.getSellPrice());
        dto.setColors(Optional.ofNullable(product.getColor()).map(Color::getColorJson).orElse(null));
        dto.setSizes(Optional.ofNullable(product.getSize()).map(Size::getSizeJson).orElse(null));
        dto.setDescription(product.getDescription());
        dto.setSerial(product.getSerial());
        dto.setWarranty(product.getWarranty());
        dto.setImages(Optional.ofNullable(product.getImage()).map(Image::getImageJson).orElse(null));
        dto.setAttributes(Optional.ofNullable(product.getAttribute()).map(Attribute::getAttributeJson).orElse(null));
        dto.setCreatedAt(product.getCreatedAt());
        dto.setAvgRating(calculateAverageRating(product.getReviews()));
        dto.setReviewCount(product.getReviews().size());
        // Calculate promotion price
        BigDecimal promotionPrice = calculatePromotionPrice(product.getId(), product.getSellPrice());
        dto.setPromotionPrice(promotionPrice);
        dto.setInventoryQuantity(product.getInventoryQuantity());
        if(product.getStatus()==1){
            dto.setStatus("Còn hàng");
        }else if(product.getStatus()==2){
            dto.setStatus("Hết hàng");
        }else if(product.getStatus()==3){
            dto.setStatus("Ngừng sản xuất");
        }else if(product.getStatus()==4){
            dto.setStatus("Sắp ra mắt");
        }else {
            dto.setStatus("Đang cập nhật");
        }
        if (promotionPrice.compareTo(product.getSellPrice()) < 0) {
            dto.setPromotionEndDate(getEarliestPromotionEndDate(product.getId()));

            ProductPromotion activePromotion = getActivePromotion(product.getId());
            if (activePromotion != null) {
                Promotion promotion = activePromotion.getPromotion();
                String formattedDiscount;
                if (promotion.getDiscountType()) {
                    DecimalFormat dfPercent = new DecimalFormat("#");
                    formattedDiscount = dfPercent.format(promotion.getDiscountValue()) + "%";
                } else {
                    DecimalFormat dfCurrency = new DecimalFormat("#,###");
                    formattedDiscount = dfCurrency.format(promotion.getDiscountValue()) + "đ";
                }
                dto.setFormattedDiscount(formattedDiscount);
            }
        }
        return dto;
    }

    private int calculateAverageRating(Set<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return 0;
        }

        double avgRating = reviews.stream()
                .mapToInt(review -> review.getRating().intValue())
                .average()
                .orElse(0.0);

        // Custom rounding logic:
        // If decimal part < 0.5, round down
        // If decimal part >= 0.5, round up
        return (int) Math.round(avgRating);
    }

    private BigDecimal calculatePromotionPrice(String productId, BigDecimal sellPrice) {
        List<ProductPromotion> productPromotions = productPromotionRepository.findByProductIdWithPromotion(productId);
        BigDecimal lowestPrice = sellPrice;

        for (ProductPromotion pp : productPromotions) {
            Promotion promotion = pp.getPromotion();
            if (isPromotionActive(promotion)) {
                BigDecimal discountedPrice;
                if (promotion.getDiscountType()) {
                    // Percentage discount
                    BigDecimal discountFactor = BigDecimal.ONE.subtract(
                            BigDecimal.valueOf(promotion.getDiscountValue()).divide(BigDecimal.valueOf(100)));
                    discountedPrice = sellPrice.multiply(discountFactor);
                } else {
                    // Fixed amount discount
                    discountedPrice = sellPrice.subtract(BigDecimal.valueOf(promotion.getDiscountValue()));
                }
                discountedPrice = discountedPrice.max(BigDecimal.ZERO);
                if (discountedPrice.compareTo(lowestPrice) < 0) {
                    lowestPrice = discountedPrice;
                }
            }
        }
        return lowestPrice;
    }
    private ProductPromotion getActivePromotion(String productId) {
        List<ProductPromotion> productPromotions = productPromotionRepository.findByProductIdWithPromotion(productId);
        return productPromotions.stream()
                .filter(pp -> isPromotionActive(pp.getPromotion()))
                .findFirst()
                .orElse(null);
    }
    private boolean isPromotionActive(Promotion promotion) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = LocalDateTime.ofInstant(promotion.getStartDate(), ZoneId.systemDefault());
        LocalDateTime endDate = LocalDateTime.ofInstant(promotion.getEndDate(), ZoneId.systemDefault());
        return now.isAfter(startDate) && now.isBefore(endDate);
    }

    public GetProductDto getProductById(String id) {
        Product product = productRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        return convertToDTO(product);
    }

    public Page<GetProductDto> getProductsByCategory(Integer categoryId, int page, int size, List<String> brands, List<String> attributes) {
        List<Product> products = productRepository.findAllByCategoryIdWithDetails(categoryId);
        if (brands != null && !brands.isEmpty()) {
            products = products.stream()
                    .filter(product -> brands.contains(product.getBrand()))
                    .toList();
        }
        if (attributes != null && !attributes.isEmpty()) {
            products = products.stream()
                    .filter(product -> attributes.contains(product.getAttribute().getAttributeJson()))
                    .toList();
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productsPage = new PageImpl<>(products, pageable, products.size());
        return productsPage.map(this::convertToDTO);
    }

    public List<GetProductDto> getProductsOnSale() {
        List<Product> allProducts = productRepository.findAllWithDetails();

        return allProducts.stream()
                .filter(product -> {
                    List<ProductPromotion> promotions = productPromotionRepository.findByProductIdWithPromotion(product.getId());
                    return promotions.stream()
                            .anyMatch(pp -> isPromotionActive(pp.getPromotion()));
                })
                .map(this::convertToDTO)
                .limit(8)
                .collect(Collectors.toList());
    }

    public Instant getEarliestPromotionEndDate(String productId) {
        List<ProductPromotion> promotions = productPromotionRepository.findByProductIdWithPromotion(productId);
        return promotions.stream()
                .filter(pp -> isPromotionActive(pp.getPromotion()))
                .map(pp -> pp.getPromotion().getEndDate())
                .min(Instant::compareTo)
                .orElse(null);
    }

    public List<GetProductDto> getNewestProducts() {
        List<Product> products = productRepository.findAllWithDetails();
        return products.stream()
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt())) // Sort by creation date, newest first
                .limit(4) // Get only 4 products
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<GetProductDto> getTopSellingProducts(int limit) {
        Pageable topN = PageRequest.of(0, limit);
        List<Product> topSellingProducts = productRepository.findTopSellingProducts(topN);
        return topSellingProducts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<String> getBrandsByCategory(Long categoryId) {
        return productRepository.findDistinctBrandsByCategory_Id(categoryId);
    }

    public List<GetProductDto> getAllProductsByCategory(Integer categoryId, List<String> brands) {
        List<Product> products = productRepository.findAllByCategoryIdWithDetails(categoryId);
        if (brands != null && !brands.isEmpty()) {
            for (String brand : brands) {
                products = products.stream()
                        .filter(product -> product.getBrand().equals(brand))
                        .toList();
            }
        }
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<GetProductDto> getAllProductsByCategory(Integer categoryId) {
        List<Product> products = productRepository.findAllByCategoryIdWithDetails(categoryId);
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<GetProductDto> getRelatedProducts(String productId, int limit) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        List<Product> relatedProducts = productRepository.findByCategoryIdAndIdNot(
                product.getCategory().getId(), productId);

        return relatedProducts.stream()
                .sorted((p1, p2) -> {
                    if (isSameBrandAndAttribute(p1, product) && isSameBrandAndAttribute(p2, product)) {
                        return 0;
                    } else if (isSameBrandAndAttribute(p1, product)) {
                        return -1;
                    } else if (isSameBrandAndAttribute(p2, product)) {
                        return 1;
                    }
                    if (p1.getBrand().equals(product.getBrand()) && p2.getBrand().equals(product.getBrand())) {
                        return 0;
                    } else if (p1.getBrand().equals(product.getBrand())) {
                        return -1;
                    } else if (p2.getBrand().equals(product.getBrand())) {
                        return 1;
                    }
                    return 0;
                })
                .limit(limit)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private boolean isSameBrandAndAttribute(Product p1, Product p2) {
        return p1.getBrand().equals(p2.getBrand()) &&
                p1.getAttribute() != null && p2.getAttribute() != null &&
                p1.getAttribute().getAttributeJson().equals(p2.getAttribute().getAttributeJson());
    }
}
