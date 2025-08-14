package com.mycompany.blockkbusterr.service;

import com.mycompany.blockkbusterr.entity.Movie;
import com.mycompany.blockkbusterr.entity.Review;
import com.mycompany.blockkbusterr.entity.User;
import com.mycompany.blockkbusterr.repository.MovieRepository;
import com.mycompany.blockkbusterr.repository.ReviewRepository;
import com.mycompany.blockkbusterr.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class ReviewService {
    
    @Inject
    private ReviewRepository reviewRepository;
    
    @Inject
    private UserRepository userRepository;
    
    @Inject
    private MovieRepository movieRepository;
    
    /**
     * Add a new review
     */
    public Review addReview(Long userId, Long movieId, Integer rating, String comment) {
        // Validate user
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        User user = userOpt.get();
        
        if (!user.getActive()) {
            throw new IllegalArgumentException("User account is not active");
        }
        
        // Validate movie
        Optional<Movie> movieOpt = movieRepository.findById(movieId);
        if (movieOpt.isEmpty()) {
            throw new IllegalArgumentException("Movie not found");
        }
        Movie movie = movieOpt.get();
        
        if (!movie.getActive()) {
            throw new IllegalArgumentException("Movie is not active");
        }
        
        // Validate rating
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        
        // Check if user has already reviewed this movie
        if (reviewRepository.hasUserReviewedMovie(userId, movieId)) {
            throw new IllegalArgumentException("User has already reviewed this movie");
        }
        
        // Create review
        Review review = new Review();
        review.setUser(user);
        review.setMovie(movie);
        review.setRating(rating);
        review.setComment(comment != null ? comment.trim() : null);
        review.setActive(true);
        
        return reviewRepository.save(review);
    }
    
    /**
     * Update an existing review
     */
    public Review updateReview(Long reviewId, Integer rating, String comment) {
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);
        if (reviewOpt.isEmpty()) {
            throw new IllegalArgumentException("Review not found");
        }
        
        Review review = reviewOpt.get();
        
        if (!review.getActive()) {
            throw new IllegalArgumentException("Review is not active");
        }
        
        // Validate rating
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        
        // Update review
        review.setRating(rating);
        review.setComment(comment != null ? comment.trim() : null);
        
        return reviewRepository.update(review);
    }
    
    /**
     * Find review by ID
     */
    public Optional<Review> findReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId);
    }
    
    /**
     * Get all reviews
     */
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }
    
    /**
     * Get reviews by movie
     */
    public List<Review> getReviewsByMovie(Long movieId) {
        return reviewRepository.findByMovieId(movieId);
    }
    
    /**
     * Get reviews by user
     */
    public List<Review> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserId(userId);
    }
    
    /**
     * Get reviews by rating
     */
    public List<Review> getReviewsByRating(Integer rating) {
        return reviewRepository.findByRating(rating);
    }
    
    /**
     * Get reviews by rating range
     */
    public List<Review> getReviewsByRatingRange(int minRating, int maxRating) {
        return reviewRepository.findByRatingRange(minRating, maxRating);
    }
    
    /**
     * Get recent reviews
     */
    public List<Review> getRecentReviews(int days) {
        return reviewRepository.findRecentReviews(days);
    }
    
    /**
     * Get recent reviews with limit
     */
    public List<Review> getRecentReviewsLimited(int limit) {
        return reviewRepository.findRecentReviewsLimited(limit);
    }
    
    /**
     * Get top rated reviews for a movie
     */
    public List<Review> getTopRatedReviewsForMovie(Long movieId, int limit) {
        return reviewRepository.findTopRatedReviewsForMovie(movieId, limit);
    }
    
    /**
     * Get reviews with comments
     */
    public List<Review> getReviewsWithComments() {
        return reviewRepository.findReviewsWithComments();
    }
    
    /**
     * Get reviews with comments for a movie
     */
    public List<Review> getReviewsWithCommentsForMovie(Long movieId) {
        return reviewRepository.findReviewsWithCommentsForMovie(movieId);
    }
    
    /**
     * Get most helpful reviews
     */
    public List<Review> getMostHelpfulReviews(int limit) {
        return reviewRepository.findMostHelpfulReviews(limit);
    }
    
    /**
     * Get average rating for a movie
     */
    public double getAverageRatingForMovie(Long movieId) {
        Double avgRating = reviewRepository.getAverageRatingForMovie(movieId);
        return avgRating != null ? avgRating : 0.0;
    }
    
    /**
     * Get review count for a movie
     */
    public long getReviewCountForMovie(Long movieId) {
        return reviewRepository.countReviewsForMovie(movieId);
    }
    
    /**
     * Get review count by user
     */
    public long getReviewCountByUser(Long userId) {
        return reviewRepository.countReviewsByUser(userId);
    }
    
    /**
     * Get rating distribution for a movie
     */
    public List<RatingDistribution> getRatingDistributionForMovie(Long movieId) {
        List<Object[]> results = reviewRepository.getRatingDistributionForMovie(movieId);
        return results.stream()
                .map(result -> new RatingDistribution((Integer) result[0], ((Number) result[1]).longValue()))
                .toList();
    }
    
    /**
     * Check if user has reviewed a movie
     */
    public boolean hasUserReviewedMovie(Long userId, Long movieId) {
        return reviewRepository.hasUserReviewedMovie(userId, movieId);
    }
    
    /**
     * Get user's review for a movie
     */
    public Optional<Review> getUserReviewForMovie(Long userId, Long movieId) {
        return reviewRepository.findByUserIdAndMovieId(userId, movieId);
    }
    
    /**
     * Delete review (soft delete)
     */
    public boolean deleteReview(Long reviewId) {
        return reviewRepository.softDeleteReview(reviewId);
    }
    
    /**
     * Reactivate review
     */
    public boolean reactivateReview(Long reviewId) {
        return reviewRepository.reactivateReview(reviewId);
    }
    
    /**
     * Check if user can review movie
     */
    public boolean canUserReviewMovie(Long userId, Long movieId) {
        // Check if user exists and is active
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty() || !userOpt.get().getActive()) {
            return false;
        }
        
        // Check if movie exists and is active
        Optional<Movie> movieOpt = movieRepository.findById(movieId);
        if (movieOpt.isEmpty() || !movieOpt.get().getActive()) {
            return false;
        }
        
        // Check if user has already reviewed this movie
        return !reviewRepository.hasUserReviewedMovie(userId, movieId);
    }
    
    /**
     * Get movie review summary
     */
    public MovieReviewSummary getMovieReviewSummary(Long movieId) {
        double averageRating = getAverageRatingForMovie(movieId);
        long totalReviews = getReviewCountForMovie(movieId);
        List<RatingDistribution> distribution = getRatingDistributionForMovie(movieId);
        
        return new MovieReviewSummary(movieId, averageRating, totalReviews, distribution);
    }
    
    /**
     * Get user review summary
     */
    public UserReviewSummary getUserReviewSummary(Long userId) {
        long totalReviews = getReviewCountByUser(userId);
        List<Review> userReviews = getReviewsByUser(userId);
        
        double averageRating = userReviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        
        return new UserReviewSummary(userId, totalReviews, averageRating);
    }
    
    /**
     * Get review statistics
     */
    public ReviewStats getReviewStats() {
        long totalReviews = reviewRepository.count();
        
        long fiveStarReviews = reviewRepository.countReviewsByRating(5);
        long fourStarReviews = reviewRepository.countReviewsByRating(4);
        long threeStarReviews = reviewRepository.countReviewsByRating(3);
        long twoStarReviews = reviewRepository.countReviewsByRating(2);
        long oneStarReviews = reviewRepository.countReviewsByRating(1);
        
        List<Review> reviewsWithComments = reviewRepository.findReviewsWithComments();
        long reviewsWithCommentsCount = reviewsWithComments.size();
        
        return new ReviewStats(totalReviews, fiveStarReviews, fourStarReviews, 
                              threeStarReviews, twoStarReviews, oneStarReviews, 
                              reviewsWithCommentsCount);
    }
    
    // Inner classes for data transfer
    public static class RatingDistribution {
        private final int rating;
        private final long count;
        
        public RatingDistribution(int rating, long count) {
            this.rating = rating;
            this.count = count;
        }
        
        public int getRating() { return rating; }
        public long getCount() { return count; }
    }
    
    public static class MovieReviewSummary {
        private final Long movieId;
        private final double averageRating;
        private final long totalReviews;
        private final List<RatingDistribution> ratingDistribution;
        
        public MovieReviewSummary(Long movieId, double averageRating, long totalReviews, List<RatingDistribution> ratingDistribution) {
            this.movieId = movieId;
            this.averageRating = averageRating;
            this.totalReviews = totalReviews;
            this.ratingDistribution = ratingDistribution;
        }
        
        public Long getMovieId() { return movieId; }
        public double getAverageRating() { return averageRating; }
        public long getTotalReviews() { return totalReviews; }
        public List<RatingDistribution> getRatingDistribution() { return ratingDistribution; }
        
        public String getFormattedRating() {
            return String.format("%.1f", averageRating);
        }
    }
    
    public static class UserReviewSummary {
        private final Long userId;
        private final long totalReviews;
        private final double averageRating;
        
        public UserReviewSummary(Long userId, long totalReviews, double averageRating) {
            this.userId = userId;
            this.totalReviews = totalReviews;
            this.averageRating = averageRating;
        }
        
        public Long getUserId() { return userId; }
        public long getTotalReviews() { return totalReviews; }
        public double getAverageRating() { return averageRating; }
    }
    
    public static class ReviewStats {
        private final long totalReviews;
        private final long fiveStarReviews;
        private final long fourStarReviews;
        private final long threeStarReviews;
        private final long twoStarReviews;
        private final long oneStarReviews;
        private final long reviewsWithComments;
        
        public ReviewStats(long totalReviews, long fiveStarReviews, long fourStarReviews,
                          long threeStarReviews, long twoStarReviews, long oneStarReviews,
                          long reviewsWithComments) {
            this.totalReviews = totalReviews;
            this.fiveStarReviews = fiveStarReviews;
            this.fourStarReviews = fourStarReviews;
            this.threeStarReviews = threeStarReviews;
            this.twoStarReviews = twoStarReviews;
            this.oneStarReviews = oneStarReviews;
            this.reviewsWithComments = reviewsWithComments;
        }
        
        public long getTotalReviews() { return totalReviews; }
        public long getFiveStarReviews() { return fiveStarReviews; }
        public long getFourStarReviews() { return fourStarReviews; }
        public long getThreeStarReviews() { return threeStarReviews; }
        public long getTwoStarReviews() { return twoStarReviews; }
        public long getOneStarReviews() { return oneStarReviews; }
        public long getReviewsWithComments() { return reviewsWithComments; }
        
        public double getAverageRating() {
            if (totalReviews == 0) return 0.0;
            long totalRatingPoints = (fiveStarReviews * 5) + (fourStarReviews * 4) + 
                                   (threeStarReviews * 3) + (twoStarReviews * 2) + (oneStarReviews * 1);
            return (double) totalRatingPoints / totalReviews;
        }
    }
}