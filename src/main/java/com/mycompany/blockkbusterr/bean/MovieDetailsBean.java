package com.mycompany.blockkbusterr.bean;

import com.mycompany.blockkbusterr.entity.Movie;
import com.mycompany.blockkbusterr.entity.Review;
import com.mycompany.blockkbusterr.entity.User;
import com.mycompany.blockkbusterr.service.MovieService;
import com.mycompany.blockkbusterr.service.ReviewService;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Named
@RequestScoped
public class MovieDetailsBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    private MovieService movieService;
    
    @EJB
    private ReviewService reviewService;
    
    @Inject
    private SessionBean sessionBean;
    
    private Movie movie;
    private List<Review> movieReviews;
    private String newReviewComment;
    private Integer newReviewRating;
    private boolean userHasReviewed;
    
    @PostConstruct
    public void init() {
        // Get movie ID from request parameter
        String movieIdParam = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap()
                .get("movieId");
        
        if (movieIdParam != null && !movieIdParam.isEmpty()) {
            try {
                Long movieId = Long.parseLong(movieIdParam);
                loadMovieDetails(movieId);
            } catch (NumberFormatException e) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Invalid movie ID");
                // Redirect to main page or show error
            }
        } else {
            addMessage(FacesMessage.SEVERITY_WARN, "No movie specified");
        }
    }
    
    private void loadMovieDetails(Long movieId) {
        try {
            Optional<Movie> movieOpt = movieService.findMovieById(movieId);
            if (movieOpt.isPresent()) {
                movie = movieOpt.get();
                movieReviews = reviewService.getReviewsByMovie(movieId);
                checkIfUserHasReviewed();
            } else {
                addMessage(FacesMessage.SEVERITY_ERROR, "Movie not found");
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error loading movie details: " + e.getMessage());
        }
    }
    
    private void checkIfUserHasReviewed() {
        if (sessionBean.isAuthenticated() && movie != null) {
            User currentUser = sessionBean.getCurrentUser();
            userHasReviewed = reviewService.hasUserReviewedMovie(currentUser.getUserId(), movie.getMovieId());
        }
    }
    
    public String rentMovie() {
        if (!sessionBean.isAuthenticated()) {
            addMessage(FacesMessage.SEVERITY_WARN, "Please log in to rent movies");
            return "login?faces-redirect=true";
        }
        
        if (movie == null) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Movie not found");
            return null;
        }
        
        // Redirect to loan page with movie ID
        return "loan?faces-redirect=true&movieId=" + movie.getMovieId();
    }
    
    public void submitReview() {
        if (!sessionBean.isAuthenticated()) {
            addMessage(FacesMessage.SEVERITY_WARN, "Please log in to submit reviews");
            return;
        }
        
        if (movie == null) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Movie not found");
            return;
        }
        
        if (userHasReviewed) {
            addMessage(FacesMessage.SEVERITY_WARN, "You have already reviewed this movie");
            return;
        }
        
        if (newReviewRating == null || newReviewRating < 1 || newReviewRating > 5) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Please select a rating between 1 and 5 stars");
            return;
        }
        
        if (newReviewComment == null || newReviewComment.trim().isEmpty()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Please enter a review comment");
            return;
        }
        
        try {
            User currentUser = sessionBean.getCurrentUser();
            Review review = new Review(currentUser, movie, newReviewRating, newReviewComment.trim());
            
            reviewService.addReview(currentUser.getUserId(), movie.getMovieId(), newReviewRating, newReviewComment.trim());
            
            // Refresh reviews and reset form
            movieReviews = reviewService.getReviewsByMovie(movie.getMovieId());
            userHasReviewed = true;
            newReviewComment = null;
            newReviewRating = null;
            
            addMessage(FacesMessage.SEVERITY_INFO, "Review submitted successfully!");
            
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error submitting review: " + e.getMessage());
        }
    }
    
    private void addMessage(FacesMessage.Severity severity, String message) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, message, null));
    }
    
    // Getters and Setters
    public Movie getMovie() {
        return movie;
    }
    
    public void setMovie(Movie movie) {
        this.movie = movie;
    }
    
    public List<Review> getMovieReviews() {
        return movieReviews;
    }
    
    public void setMovieReviews(List<Review> movieReviews) {
        this.movieReviews = movieReviews;
    }
    
    public String getNewReviewComment() {
        return newReviewComment;
    }
    
    public void setNewReviewComment(String newReviewComment) {
        this.newReviewComment = newReviewComment;
    }
    
    public Integer getNewReviewRating() {
        return newReviewRating;
    }
    
    public void setNewReviewRating(Integer newReviewRating) {
        this.newReviewRating = newReviewRating;
    }
    
    public boolean isUserHasReviewed() {
        return userHasReviewed;
    }
    
    public void setUserHasReviewed(boolean userHasReviewed) {
        this.userHasReviewed = userHasReviewed;
    }
    
    // Utility methods for the UI
    public boolean isMovieLoaded() {
        return movie != null;
    }
    
    public boolean isMovieAvailable() {
        return movie != null && movie.isAvailable();
    }
    
    public boolean hasReviews() {
        return movieReviews != null && !movieReviews.isEmpty();
    }
    
    public String getMovieImageUrl() {
        if (movie != null && movie.getImageUrl() != null && !movie.getImageUrl().trim().isEmpty()) {
            return movie.getImageUrl();
        }
        return "resources/img/movie-placeholder.jpg";
    }
    
    public String getAverageRatingFormatted() {
        if (movie == null) return "No ratings";
        double avgRating = movie.getAverageRating();
        if (avgRating == 0.0) return "No ratings";
        return String.format("%.1f/5.0", avgRating);
    }
    
    public String getRatingStarsDisplay(int rating) {
        StringBuilder stars = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            if (i <= rating) {
                stars.append("★");
            } else {
                stars.append("☆");
            }
        }
        return stars.toString();
    }
}