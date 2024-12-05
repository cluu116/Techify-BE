package app.techify.service;

import app.techify.entity.Review;
import app.techify.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public List<Review> getReviewsByProductId(String productId) {
        return reviewRepository.findByProductId(productId);
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