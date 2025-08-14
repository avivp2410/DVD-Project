package com.mycompany.blockkbusterr.repository;

import com.mycompany.blockkbusterr.entity.Movie;
import com.mycompany.blockkbusterr.entity.Review;
import com.mycompany.blockkbusterr.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ReviewRepository extends BaseRepository<Review, Long> {
    
    public ReviewRepository() {
        super(Review.class);
    }
    
    @Override
    protected Long getId(Review entity) {
        return entity.getReviewId();
    }
    
    /**
     * Find reviews by movie
     */
    public List<Review> findByMovie(Movie movie) {
        TypedQuery<Review> query = createNamedQuery("Review.findByMovie");
        query.setParameter("movie", movie);
        return query.getResultList();
    }
    
    /**
     * Find reviews by movie ID
     */
    public List<Review> findByMovieId(Long movieId) {
        String jpql = "SELECT r FROM Review r WHERE r.movie.movieId = :movieId AND r.active = true ORDER BY r.reviewDate DESC";
        TypedQuery<Review> query = entityManager.createQuery(jpql, Review.class);
        query.setParameter("movieId", movieId);
        return query.getResultList();
    }
    
    /**
     * Find reviews by user
     */
    public List<Review> findByUser(User user) {
        TypedQuery<Review> query = createNamedQuery("Review.findByUser");
        query.setParameter("user", user);
        return query.getResultList();
    }
    
    /**
     * Find reviews by user ID
     */
    public List<Review> findByUserId(Long userId) {
        String jpql = "SELECT r FROM Review r WHERE r.user.userId = :userId AND r.active = true ORDER BY r.reviewDate DESC";
        TypedQuery<Review> query = entityManager.createQuery(jpql, Review.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
    
    /**
     * Find reviews by rating
     */
    public List<Review> findByRating(Integer rating) {
        TypedQuery<Review> query = createNamedQuery("Review.findByRating");
        query.setParameter("rating", rating);
        return query.getResultList();
    }
    
    /**
     * Find review by user and movie
     */
    public Optional<Review> findByUserAndMovie(User user, Movie movie) {
        try {
            TypedQuery<Review> query = createNamedQuery("Review.findByUserAndMovie");
            query.setParameter("user", user);
            query.setParameter("movie", movie);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    
    /**
     * Find review by user ID and movie ID
     */
    public Optional<Review> findByUserIdAndMovieId(Long userId, Long movieId) {
        try {
            String jpql = "SELECT r FROM Review r WHERE r.user.userId = :userId AND r.movie.movieId = :movieId";
            TypedQuery<Review> query = entityManager.createQuery(jpql, Review.class);
            query.setParameter("userId", userId);
            query.setParameter("movieId", movieId);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    
    /**
     * Find reviews by rating range
     */
    public List<Review> findByRatingRange(int minRating, int maxRating) {
        String jpql = "SELECT r FROM Review r WHERE r.rating BETWEEN :minRating AND :maxRating AND r.active = true ORDER BY r.reviewDate DESC";
        TypedQuery<Review> query = entityManager.createQuery(jpql, Review.class);
        query.setParameter("minRating", minRating);
        query.setParameter("maxRating", maxRating);
        return query.getResultList();
    }
    
    /**
     * Find recent reviews (last N days)
     */
    public List<Review> findRecentReviews(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        String jpql = "SELECT r FROM Review r WHERE r.reviewDate >= :cutoffDate AND r.active = true ORDER BY r.reviewDate DESC";
        TypedQuery<Review> query = entityManager.createQuery(jpql, Review.class);
        query.setParameter("cutoffDate", cutoffDate);
        return query.getResultList();
    }
    
    /**
     * Find recent reviews with limit
     */
    public List<Review> findRecentReviews(int limit) {
        String jpql = "SELECT r FROM Review r WHERE r.active = true ORDER BY r.reviewDate DESC";
        TypedQuery<Review> query = entityManager.createQuery(jpql, Review.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    /**
     * Find top rated reviews for a movie
     */
    public List<Review> findTopRatedReviewsForMovie(Long movieId, int limit) {
        String jpql = "SELECT r FROM Review r WHERE r.movie.movieId = :movieId AND r.active = true ORDER BY r.rating DESC, r.reviewDate DESC";
        TypedQuery<Review> query = entityManager.createQuery(jpql, Review.class);
        query.setParameter("movieId", movieId);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    /**
     * Find reviews with comments
     */
    public List<Review> findReviewsWithComments() {
        String jpql = "SELECT r FROM Review r WHERE r.comment IS NOT NULL AND TRIM(r.comment) != '' AND r.active = true ORDER BY r.reviewDate DESC";
        TypedQuery<Review> query = entityManager.createQuery(jpql, Review.class);
        return query.getResultList();
    }
    
    /**
     * Find reviews with comments for a movie
     */
    public List<Review> findReviewsWithCommentsForMovie(Long movieId) {
        String jpql = "SELECT r FROM Review r WHERE r.movie.movieId = :movieId AND r.comment IS NOT NULL AND TRIM(r.comment) != '' AND r.active = true ORDER BY r.reviewDate DESC";
        TypedQuery<Review> query = entityManager.createQuery(jpql, Review.class);
        query.setParameter("movieId", movieId);
        return query.getResultList();
    }
    
    /**
     * Get average rating for a movie
     */
    public Double getAverageRatingForMovie(Long movieId) {
        String jpql = "SELECT AVG(CAST(r.rating AS double)) FROM Review r WHERE r.movie.movieId = :movieId AND r.active = true";
        TypedQuery<Double> query = entityManager.createQuery(jpql, Double.class);
        query.setParameter("movieId", movieId);
        Double result = query.getSingleResult();
        return result != null ? result : 0.0;
    }
    
    /**
     * Count reviews for a movie
     */
    public long countReviewsForMovie(Long movieId) {
        String jpql = "SELECT COUNT(r) FROM Review r WHERE r.movie.movieId = :movieId AND r.active = true";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("movieId", movieId);
        return query.getSingleResult();
    }
    
    /**
     * Count reviews by user
     */
    public long countReviewsByUser(Long userId) {
        String jpql = "SELECT COUNT(r) FROM Review r WHERE r.user.userId = :userId AND r.active = true";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("userId", userId);
        return query.getSingleResult();
    }
    
    /**
     * Count reviews by rating
     */
    public long countReviewsByRating(Integer rating) {
        String jpql = "SELECT COUNT(r) FROM Review r WHERE r.rating = :rating AND r.active = true";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("rating", rating);
        return query.getSingleResult();
    }
    
    /**
     * Get rating distribution for a movie
     */
    public List<Object[]> getRatingDistributionForMovie(Long movieId) {
        String jpql = "SELECT r.rating, COUNT(r) FROM Review r WHERE r.movie.movieId = :movieId AND r.active = true GROUP BY r.rating ORDER BY r.rating DESC";
        TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
        query.setParameter("movieId", movieId);
        return query.getResultList();
    }
    
    /**
     * Check if user has reviewed a movie
     */
    public boolean hasUserReviewedMovie(Long userId, Long movieId) {
        return findByUserIdAndMovieId(userId, movieId).isPresent();
    }
    
    /**
     * Update review rating and comment
     */
    public boolean updateReview(Long reviewId, Integer rating, String comment) {
        String jpql = "UPDATE Review r SET r.rating = :rating, r.comment = :comment, r.updatedAt = :updatedAt WHERE r.reviewId = :reviewId";
        int updatedRows = entityManager.createQuery(jpql)
                .setParameter("rating", rating)
                .setParameter("comment", comment)
                .setParameter("updatedAt", LocalDateTime.now())
                .setParameter("reviewId", reviewId)
                .executeUpdate();
        return updatedRows > 0;
    }
    
    /**
     * Soft delete review (mark as inactive)
     */
    public boolean softDeleteReview(Long reviewId) {
        String jpql = "UPDATE Review r SET r.active = false WHERE r.reviewId = :reviewId";
        int updatedRows = entityManager.createQuery(jpql)
                .setParameter("reviewId", reviewId)
                .executeUpdate();
        return updatedRows > 0;
    }
    
    /**
     * Reactivate review
     */
    public boolean reactivateReview(Long reviewId) {
        String jpql = "UPDATE Review r SET r.active = true WHERE r.reviewId = :reviewId";
        int updatedRows = entityManager.createQuery(jpql)
                .setParameter("reviewId", reviewId)
                .executeUpdate();
        return updatedRows > 0;
    }
    
    /**
     * Find most helpful reviews (highest rated reviews with comments)
     */
    public List<Review> findMostHelpfulReviews(int limit) {
        String jpql = "SELECT r FROM Review r WHERE r.comment IS NOT NULL AND TRIM(r.comment) != '' AND r.active = true ORDER BY r.rating DESC, LENGTH(r.comment) DESC, r.reviewDate DESC";
        TypedQuery<Review> query = entityManager.createQuery(jpql, Review.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
}