package com.mycompany.blockkbusterr.dto;

import com.mycompany.blockkbusterr.entity.Movie;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "movieResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class MovieResponse {
    
    @XmlElement
    private Long movieId;
    
    @XmlElement
    private String title;
    
    @XmlElement
    private String releaseDate;
    
    @XmlElement
    private Integer duration;
    
    @XmlElement
    private String genre;
    
    @XmlElement
    private Integer quantity;
    
    @XmlElement
    private String description;
    
    
    @XmlElement
    private Boolean active;
    
    @XmlElement
    private String createdAt;
    
    @XmlElement
    private Double averageRating;
    
    @XmlElement
    private Long reviewCount;
    
    @XmlElement
    private boolean success;
    
    @XmlElement
    private String message;
    
    // Default constructor for JAXB
    public MovieResponse() {}
    
    public MovieResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public MovieResponse(Movie movie, boolean success, String message) {
        this.success = success;
        this.message = message;
        if (movie != null) {
            this.movieId = movie.getMovieId();
            this.title = movie.getTitle();
            this.releaseDate = movie.getReleaseDate() != null ? movie.getReleaseDate().toString() : null;
            this.duration = movie.getDuration();
            this.genre = movie.getGenre();
            this.quantity = movie.getQuantity();
            this.description = movie.getDescription();
            this.active = movie.getActive();
            this.createdAt = movie.getCreatedAt() != null ? movie.getCreatedAt().toString() : null;
        }
    }
    
    // Static factory methods
    public static MovieResponse success(Movie movie) {
        return new MovieResponse(movie, true, "Operation successful");
    }
    
    public static MovieResponse success(Movie movie, String message) {
        return new MovieResponse(movie, true, message);
    }
    
    public static MovieResponse error(String message) {
        return new MovieResponse(false, message);
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
    
    public String getReleaseDate() {
        return releaseDate;
    }
    
    public void setReleaseDate(String releaseDate) {
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
    
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public Double getAverageRating() {
        return averageRating;
    }
    
    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }
    
    public Long getReviewCount() {
        return reviewCount;
    }
    
    public void setReviewCount(Long reviewCount) {
        this.reviewCount = reviewCount;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
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
    
    public boolean isAvailable() {
        return quantity != null && quantity > 0;
    }
}