package app.techify.service;

import app.techify.dto.ReviewDto;
import app.techify.entity.Review;
import app.techify.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public List<ReviewDto> getReviewsByProductId(String productId) {
        return reviewRepository.findByProductId(productId).stream().map(rv-> new ReviewDto(
                rv.getId(),
                rv.getRating(),
                rv.getComment(),
                rv.getProduct().getId(),
                rv.getCustomer().getFullName(),
                rv.getCustomer().getId(),
                rv.getCustomer().getAccount().getAvatar(),
                rv.getCreatedAt()
        )).collect(Collectors.toList());
    }

    public Review createReview(Review review) {
        review.setCreatedAt(Instant.now());
        return reviewRepository.save(review);
    }

    public Review updateReview(Review review) {
        Review existingReview = reviewRepository.findById(review.getId())
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + review.getId()));
        
        existingReview.setRating(review.getRating());
        existingReview.setComment(review.getComment());
        
        return reviewRepository.save(existingReview);
    }

    public void deleteReview(Integer id) {
        if (!reviewRepository.existsById(id)) {
            throw new RuntimeException("Review not found with id: " + id);
        }
        reviewRepository.deleteById(id);
    }
} 