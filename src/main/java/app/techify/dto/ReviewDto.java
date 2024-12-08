package app.techify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDto {
    private Integer id;
    private Short rating;
    private String comment;
    private String productId;
    private String customerName;
    private String customerId;
    private String avatar;
    private Instant createdAt;
}
