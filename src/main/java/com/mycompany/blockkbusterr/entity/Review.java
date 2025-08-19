package com.mycompany.blockkbusterr.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@NamedQueries({
    @NamedQuery(name = "Review.findAll", query = "SELECT r FROM Review r ORDER BY r.reviewDate DESC"),
    @NamedQuery(name = "Review.findByMovie", query = "SELECT r FROM Review r WHERE r.movie = :movie ORDER BY r.reviewDate DESC"),
    @NamedQuery(name = "Review.findByUser", query = "SELECT r FROM Review r WHERE r.user = :user ORDER BY r.reviewDate DESC"),
    @NamedQuery(name = "Review.findByRating", query = "SELECT r FROM Review r WHERE r.rating = :rating ORDER BY r.reviewDate DESC"),
    @NamedQuery(name = "Review.findByUserAndMovie", query = "SELECT r FROM Review r WHERE r.user = :user AND r.movie = :movie")
})
public class Review implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    @NotNull(message = "Movie is required")
    private Movie movie;
    
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    @Column(name = "rating", nullable = false)
    private Integer rating;
    
    @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
    @Column(name = "comment", length = 1000)
    private String comment;
    
    @Column(name = "review_date", nullable = false)
    private LocalDateTime reviewDate;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "active", nullable = false)
    private Boolean active = true;
    
    // Constructors
    public Review() {
        this.reviewDate = LocalDateTime.now();
    }
    
    public Review(User user, Movie movie, Integer rating) {
        this();
        this.user = user;
        this.movie = movie;
        this.rating = rating;
    }
    
    public Review(User user, Movie movie, Integer rating, String comment) {
        this(user, movie, rating);
        this.comment = comment;
    }
    
    // Lifecycle callbacks
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getReviewId() {
        return reviewId;
    }
    
    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Movie getMovie() {
        return movie;
    }
    
    public void setMovie(Movie movie) {
        this.movie = movie;
    }
    
    public Integer getRating() {
        return rating;
    }
    
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public LocalDateTime getReviewDate() {
        return reviewDate;
    }
    
    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    // Utility methods
    public String getRatingStars() {
        if (rating == null) return "";
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < rating; i++) {
            stars.append("★");
        }
        for (int i = rating; i < 5; i++) {
            stars.append("☆");
        }
        return stars.toString();
    }
    
    public boolean hasComment() {
        return comment != null && !comment.trim().isEmpty();
    }
    
    public boolean getHasComment() {
        return hasComment();
    }
    
    public String getShortComment(int maxLength) {
        if (!hasComment()) return "";
        if (comment.length() <= maxLength) return comment;
        return comment.substring(0, maxLength) + "...";
    }
    
    // Overloaded method for EL compatibility (EL treats numeric literals as Long)
    public String shortComment(Long maxLength) {
        return getShortComment(maxLength.intValue());
    }
    
    // toString, equals, and hashCode
    @Override
    public String toString() {
        return "Review{" +
                "reviewId=" + reviewId +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", movie=" + (movie != null ? movie.getTitle() : "null") +
                ", rating=" + rating +
                ", reviewDate=" + reviewDate +
                ", active=" + active +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Review)) return false;
        Review review = (Review) o;
        return reviewId != null && reviewId.equals(review.reviewId);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}