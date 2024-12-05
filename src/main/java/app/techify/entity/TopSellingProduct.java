package app.techify.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;

/**
 * Mapping for DB view
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Immutable
@Table(name = "vw_top_selling_products")
public class TopSellingProduct {
    @Size(max = 20)
    @NotNull
    @Nationalized
    @Id
    @Column(name = "product_id", nullable = false, length = 20)
    private String productId;

    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "product_name", nullable = false, length = 50)
    private String productName;

    @Size(max = 255)
    @Nationalized
    @Column(name = "thumbnail")
    private String thumbnail;

    @NotNull
    @Column(name = "sell_price", nullable = false)
    private BigDecimal sellPrice;

    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "category_name", nullable = false, length = 50)
    private String categoryName;

    @Column(name = "total_quantity_sold")
    private Integer totalQuantitySold;

    @Column(name = "number_of_orders")
    private Integer numberOfOrders;

    @Column(name = "total_revenue")
    private BigDecimal totalRevenue;

}