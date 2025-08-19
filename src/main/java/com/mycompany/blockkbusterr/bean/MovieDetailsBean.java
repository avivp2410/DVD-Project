package com.mycompany.blockkbusterr.bean;

import com.mycompany.blockkbusterr.entity.Movie;
import com.mycompany.blockkbusterr.entity.Review;
import com.mycompany.blockkbusterr.entity.User;
import com.mycompany.blockkbusterr.service.MovieService;
import com.mycompany.blockkbusterr.service.ReviewService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Named
@ViewScoped
public class MovieDetailsBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private MovieService movieService;
    
    @Inject
    private ReviewService reviewService;
    
    @Inject
    private SessionBean sessionBean;
    
    @Inject
    private com.mycompany.blockkbusterr.service.RentalService rentalService;
    
    private Movie movie;
    private List<Review> movieReviews;
    private String newReviewComment;
    private Integer newReviewRating;
    private boolean userHasReviewed;
    private Long movieId; // Store movieId to preserve it across requests
    
    @PostConstruct
    public void init() {
        // Get movie ID from request parameter
        String movieIdParam = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap()
                .get("movieId");
        
        if (movieIdParam != null && !movieIdParam.isEmpty()) {
            try {
                this.movieId = Long.parseLong(movieIdParam);
                loadMovieDetails(this.movieId);
            } catch (NumberFormatException e) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Invalid movie ID");
                // Redirect to main page or show error
            }
        } else {
            addMessage(FacesMessage.SEVERITY_WARN, "No movie specified");
        }
    }
    
    private void loadMovieDetails(Long movieId) {
        System.out.println("DEBUG: loadMovieDetails() called with movieId: " + movieId);
        try {
            System.out.println("DEBUG: Calling movieService.findMovieById(" + movieId + ")");
            Optional<Movie> movieOpt = movieService.findMovieById(movieId);
            
            if (movieOpt.isPresent()) {
                movie = movieOpt.get();
                System.out.println("DEBUG: Successfully loaded movie: " + movie.getTitle() + " (ID: " + movie.getMovieId() + ")");
                movieReviews = reviewService.getReviewsByMovie(movieId);
                System.out.println("DEBUG: Loaded " + (movieReviews != null ? movieReviews.size() : "null") + " reviews");
                checkIfUserHasReviewed();
            } else {
                System.out.println("DEBUG: Movie not found with ID: " + movieId + " - movieService returned empty Optional");
                addMessage(FacesMessage.SEVERITY_ERROR, "Movie not found");
            }
        } catch (Exception e) {
            System.out.println("DEBUG: Exception in loadMovieDetails: " + e.getMessage());
            e.printStackTrace();
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
        System.out.println("DEBUG: rentMovie() called");
        System.out.println("DEBUG: movie = " + (movie == null ? "NULL" : movie.getTitle()));
        System.out.println("DEBUG: movieId = " + movieId);
        System.out.println("DEBUG: authenticated = " + sessionBean.isAuthenticated());
        
        if (!sessionBean.isAuthenticated()) {
            System.out.println("DEBUG: User not authenticated, redirecting to login");
            addMessage(FacesMessage.SEVERITY_WARN, "Please log in to rent movies");
            return "login?faces-redirect=true";
        }
        
        // Try to reload movie if it's null but we have movieId
        if (movie == null && movieId != null) {
            System.out.println("DEBUG: Movie is null but movieId exists, attempting to reload");
            loadMovieDetails(movieId);
        }
        
        if (movie == null) {
            System.out.println("DEBUG: Movie is still null, cannot proceed with rental");
            addMessage(FacesMessage.SEVERITY_ERROR, "Movie not found or session expired. Please try again.");
            return "mainPage?faces-redirect=true";
        }
        
        try {
            System.out.println("DEBUG: Using quick rental for movieId: " + movie.getMovieId());
            
            // Check if movie is available
            if (!movie.isAvailable()) {
                addMessage(FacesMessage.SEVERITY_ERROR, "This movie is currently out of stock.");
                return null;
            }
            
            // Check if user can rent this movie
            if (!rentalService.canUserRentMovie(sessionBean.getCurrentUserId(), movie.getMovieId())) {
                addMessage(FacesMessage.SEVERITY_ERROR, "You cannot rent this movie. You may already have an active rental for this movie or have reached the maximum number of rentals.");
                return null;
            }
            
            // Set return date to 1 week from now
            java.time.LocalDate oneWeekFromNow = java.time.LocalDate.now().plusWeeks(1);
            
            // Create the rental directly using RentalService
            com.mycompany.blockkbusterr.entity.Rental rental = rentalService.createRental(
                sessionBean.getCurrentUserId(),
                movie.getMovieId(),
                oneWeekFromNow
            );
            
            System.out.println("DEBUG: Quick rental created successfully: " + rental.getRentalId());
            addMessage(FacesMessage.SEVERITY_INFO, "Movie '" + movie.getTitle() + "' rented successfully for 1 week! Due back on " +
                     oneWeekFromNow.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            
            // Redirect to rental history
            return "rentalHistory?faces-redirect=true";
            
        } catch (IllegalArgumentException e) {
            System.out.println("DEBUG: Quick rental failed: " + e.getMessage());
            addMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
            return null;
        } catch (Exception e) {
            System.out.println("DEBUG: Error during quick rental: " + e.getMessage());
            e.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR, "An error occurred while processing your rental. Please try again.");
            return null;
        }
    }
    
    public void submitReview() {
        System.out.println("DEBUG: submitReview() called");
        System.out.println("DEBUG: movieId = " + movieId);
        System.out.println("DEBUG: movie = " + (movie == null ? "NULL" : movie.getTitle()));
        System.out.println("DEBUG: authenticated = " + sessionBean.isAuthenticated());
        System.out.println("DEBUG: sessionBean = " + sessionBean);
        System.out.println("DEBUG: movieService = " + movieService);
        
        if (!sessionBean.isAuthenticated()) {
            System.out.println("DEBUG: User not authenticated, returning");
            addMessage(FacesMessage.SEVERITY_WARN, "Please log in to submit reviews");
            return;
        }
        
        if (movie == null) {
            System.out.println("DEBUG: Movie is null, trying to reload with movieId: " + movieId);
            // Try to reload movie if movieId is available
            if (movieId != null) {
                System.out.println("DEBUG: movieId is not null, calling loadMovieDetails");
                loadMovieDetails(movieId);
                System.out.println("DEBUG: After reload, movie = " + (movie == null ? "NULL" : movie.getTitle()));
            } else {
                System.out.println("DEBUG: movieId is also null, cannot reload");
            }
            if (movie == null) {
                System.out.println("DEBUG: Movie still null after reload attempt - THIS IS THE ERROR LOCATION");
                System.out.println("DEBUG: Final movieId = " + movieId);
                System.out.println("DEBUG: Final movieService = " + movieService);
                addMessage(FacesMessage.SEVERITY_ERROR, "Movie not found or an error occurred loading movie details.");
                return;
            }
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
            
            System.out.println("DEBUG: Submitting review for movie: " + movie.getMovieId() + " by user: " + currentUser.getUserId());
            reviewService.addReview(currentUser.getUserId(), movie.getMovieId(), newReviewRating, newReviewComment.trim());
            
            // Refresh reviews and reset form
            System.out.println("DEBUG: Refreshing reviews after submission");
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
    
    public Long getMovieId() {
        return movieId;
    }
    
    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }
    
    // Utility methods for the UI
    public boolean isMovieLoaded() {
        return movie != null;
    }
    
    public boolean isMovieAvailable() {
        return movie != null && movie.isAvailable();
    }
    
    public boolean isHasReviews() {
        return movieReviews != null && !movieReviews.isEmpty();
    }
    
    public boolean getHasReviews() {
        return movieReviews != null && !movieReviews.isEmpty();
    }
    
    
    public String getAverageRatingFormatted() {
        if (movie == null) return "No ratings";
        
        // Log debugging information
        System.out.println("DEBUG: getAverageRatingFormatted() called for movie: " + movie.getMovieId());
        System.out.println("DEBUG: Movie reviews collection size: " + (movie.getReviews() != null ? movie.getReviews().size() : "null"));
        
        // Use ReviewService instead of Movie's lazy-loaded collection
        double avgRating = reviewService.getAverageRatingForMovie(movie.getMovieId());
        System.out.println("DEBUG: Average rating from ReviewService: " + avgRating);
        
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