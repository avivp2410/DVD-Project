package com.mycompany.blockkbusterr.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movies")
@NamedQueries({
    @NamedQuery(name = "Movie.findAll", query = "SELECT m FROM Movie m ORDER BY m.title"),
    @NamedQuery(name = "Movie.findByTitle", query = "SELECT m FROM Movie m WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%'))"),
    @NamedQuery(name = "Movie.findByGenre", query = "SELECT m FROM Movie m WHERE LOWER(m.genre) LIKE LOWER(CONCAT('%', :genre, '%'))"),
    @NamedQuery(name = "Movie.findAvailable", query = "SELECT m FROM Movie m WHERE m.quantity > 0"),
    @NamedQuery(name = "Movie.findById", query = "SELECT m FROM Movie m WHERE m.movieId = :movieId")
})
public class Movie implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Long movieId;
    
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    @NotNull(message = "Release date is required")
    @Column(name = "release_date", nullable = false)
    private LocalDate releaseDate;
    
    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Column(name = "duration", nullable = false)
    private Integer duration; // in minutes
    
    @NotBlank(message = "Genre is required")
    @Size(min = 1, max = 100, message = "Genre must be between 1 and 100 characters")
    @Column(name = "genre", nullable = false, length = 100)
    private String genre;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Column(name = "description", length = 1000)
    private String description;
    
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "active", nullable = false)
    private Boolean active = true;
    
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Rental> rentals = new ArrayList<>();
    
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();
    
    // Constructors
    public Movie() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Movie(String title, LocalDate releaseDate, Integer duration, String genre, Integer quantity) {
        this();
        this.title = title;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.genre = genre;
        this.quantity = quantity;
    }
    
    // Lifecycle callbacks
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getMovieId() {
        return movieId;
    }
    
    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public LocalDate getReleaseDate() {
        return releaseDate;
    }
    
    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }
    
    public Integer getDuration() {
        return duration;
    }
    
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    
    public String getGenre() {
        return genre;
    }
    
    public void setGenre(String genre) {
        this.genre = genre;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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
    
    public List<Rental> getRentals() {
        return rentals;
    }
    
    public void setRentals(List<Rental> rentals) {
        this.rentals = rentals;
    }
    
    public List<Review> getReviews() {
        return reviews;
    }
    
    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
    
    // Utility methods
    public boolean isAvailable() {
        return quantity != null && quantity > 0;
    }
    
    public int getReleaseYear() {
        return releaseDate != null ? releaseDate.getYear() : 0;
    }
    
    public String getDurationFormatted() {
        if (duration == null) return "Unknown";
        int hours = duration / 60;
        int minutes = duration % 60;
        if (hours > 0) {
            return hours + "h " + minutes + "m";
        } else {
            return minutes + "m";
        }
    }
    
    public double getAverageRating() {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }
    
    // toString, equals, and hashCode
    @Override
    public String toString() {
        return "Movie{" +
                "movieId=" + movieId +
                ", title='" + title + '\'' +
                ", releaseDate=" + releaseDate +
                ", duration=" + duration +
                ", genre='" + genre + '\'' +
                ", quantity=" + quantity +
                ", active=" + active +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie)) return false;
        Movie movie = (Movie) o;
        return movieId != null && movieId.equals(movie.movieId);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}