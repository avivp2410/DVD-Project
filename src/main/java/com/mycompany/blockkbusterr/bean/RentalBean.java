package com.mycompany.blockkbusterr.bean;

import com.mycompany.blockkbusterr.entity.Movie;
import com.mycompany.blockkbusterr.entity.Rental;
import com.mycompany.blockkbusterr.entity.RentalStatus;
import com.mycompany.blockkbusterr.service.MovieService;
import com.mycompany.blockkbusterr.service.RentalService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * JSF Managed Bean for handling rental operations
 */
@Named("rentalBean")
@ViewScoped
public class RentalBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(RentalBean.class.getName());
    
    @Inject
    private RentalService rentalService;
    
    @Inject
    private MovieService movieService;
    
    @Inject
    private SessionBean sessionBean;
    
    // Form fields for rental creation
    private Long movieId;
    private Movie selectedMovie;
    
    @NotNull(message = "Return date is required")
    private LocalDate returnDate;
    
    private String notes;
    
    // Rental history
    private List<Rental> userRentals;
    private List<Rental> filteredRentals;
    private String statusFilter = "ALL";
    
    // Admin functionality
    private List<Rental> allRentals;
    private List<Rental> overdueRentals;
    
    @PostConstruct
    public void init() {
        try {
            logger.info("RentalBean.init() started");
            
            // Get movie ID from URL parameter if present
            String movieIdParam = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap().get("movieId");
            
            logger.info("Movie ID parameter from URL: " + movieIdParam);
            
            if (movieIdParam != null && !movieIdParam.isEmpty()) {
                try {
                    movieId = Long.parseLong(movieIdParam);
                    logger.info("Parsed movie ID: " + movieId);
                    loadSelectedMovie();
                } catch (NumberFormatException e) {
                    logger.warning("Invalid movie ID parameter: " + movieIdParam + ", error: " + e.getMessage());
                    addErrorMessage("Invalid movie selection.");
                }
            } else {
                logger.info("No movieId parameter provided in URL - this is normal for rental history page");
            }
            
            // Set default return date (1 week from now)
            returnDate = LocalDate.now().plusWeeks(1);
            logger.info("Set default return date: " + returnDate);
            
            // Load user rentals if authenticated
            if (sessionBean.isAuthenticated()) {
                logger.info("User is authenticated, loading user rentals");
                loadUserRentals();
            } else {
                logger.info("User is not authenticated, skipping rental load");
            }
            
            logger.info("RentalBean.init() completed successfully");
            
        } catch (Exception e) {
            logger.severe("Error initializing RentalBean: " + e.getMessage());
            e.printStackTrace();
            addErrorMessage("An error occurred while loading rental information.");
        }
    }
    
    /**
     * Load the selected movie for rental
     */
    private void loadSelectedMovie() {
        logger.info("loadSelectedMovie() called with movieId: " + movieId);
        
        if (movieId != null) {
            try {
                logger.info("Calling movieService.findMovieById(" + movieId + ")");
                Optional<Movie> movieOpt = movieService.findMovieById(movieId);
                
                if (movieOpt.isPresent()) {
                    selectedMovie = movieOpt.get();
                    logger.info("Successfully loaded movie for rental: " + selectedMovie.getTitle() +
                               " (ID: " + selectedMovie.getMovieId() + ", Available: " + selectedMovie.isAvailable() +
                               ", Quantity: " + selectedMovie.getQuantity() + ")");
                } else {
                    logger.severe("Movie not found with ID: " + movieId + " - movieService returned empty Optional");
                    addErrorMessage("Movie not found.");
                }
            } catch (Exception e) {
                logger.severe("Exception occurred while loading movie ID " + movieId + ": " + e.getMessage());
                e.printStackTrace();
                addErrorMessage("An error occurred loading movie details.");
            }
        } else {
            logger.warning("loadSelectedMovie() called but movieId is null");
        }
    }
    
    /**
     * Create a new rental
     */
    public String createRental() {
        try {
            // Check authentication
            if (!sessionBean.isAuthenticated()) {
                addErrorMessage("You must be logged in to rent movies.");
                return "login.xhtml?faces-redirect=true";
            }
            
            // Validate movie selection
            if (selectedMovie == null) {
                addErrorMessage("Please select a movie to rent.");
                return null;
            }
            
            // Check if movie is available
            if (!selectedMovie.isAvailable()) {
                addErrorMessage("This movie is currently out of stock.");
                return null;
            }
            
            // Validate return date
            if (returnDate == null) {
                addErrorMessage("Please select a return date.");
                return null;
            }
            
            if (returnDate.isBefore(LocalDate.now().plusDays(1))) {
                addErrorMessage("Return date must be at least tomorrow.");
                return null;
            }
            
            if (returnDate.isAfter(LocalDate.now().plusWeeks(4))) {
                addErrorMessage("Maximum rental period is 4 weeks.");
                return null;
            }
            
            // Create the rental
            Rental rental = rentalService.createRental(
                sessionBean.getCurrentUserId(),
                selectedMovie.getMovieId(),
                returnDate
            );
            
            if (notes != null && !notes.trim().isEmpty()) {
                rental.setNotes(notes.trim());
            }
            
            logger.info("Rental created successfully: " + rental.getRentalId());
            addSuccessMessage("Movie rented successfully! Rental ID: " + rental.getRentalId());
            
            // Clear form
            clearForm();
            
            // Redirect to rental history
            return "rentalHistory.xhtml?faces-redirect=true";
            
        } catch (IllegalArgumentException e) {
            addErrorMessage(e.getMessage());
            return null;
        } catch (Exception e) {
            logger.severe("Error creating rental: " + e.getMessage());
            addErrorMessage("An error occurred while processing your rental. Please try again.");
            return null;
        }
    }
    
    /**
     * Quick rental method - rent movie for 1 week automatically
     */
    public String quickRentMovie(Long movieId) {
        try {
            logger.info("Quick rental started for movieId: " + movieId);
            
            // Check authentication
            if (!sessionBean.isAuthenticated()) {
                addErrorMessage("You must be logged in to rent movies.");
                return "login.xhtml?faces-redirect=true";
            }
            
            // Load the movie
            Optional<Movie> movieOpt = movieService.findMovieById(movieId);
            if (movieOpt.isEmpty()) {
                addErrorMessage("Movie not found.");
                return null;
            }
            
            Movie movie = movieOpt.get();
            logger.info("Loaded movie for quick rental: " + movie.getTitle());
            
            // Check if movie is available
            if (!movie.isAvailable()) {
                addErrorMessage("This movie is currently out of stock.");
                return null;
            }
            
            // Check if user can rent this movie
            if (!rentalService.canUserRentMovie(sessionBean.getCurrentUserId(), movieId)) {
                addErrorMessage("You cannot rent this movie. You may already have an active rental for this movie or have reached the maximum number of rentals.");
                return null;
            }
            
            // Set return date to 1 week from now
            LocalDate oneWeekFromNow = LocalDate.now().plusWeeks(1);
            
            // Create the rental
            Rental rental = rentalService.createRental(
                sessionBean.getCurrentUserId(),
                movieId,
                oneWeekFromNow
            );
            
            logger.info("Quick rental created successfully: " + rental.getRentalId());
            addSuccessMessage("Movie '" + movie.getTitle() + "' rented successfully for 1 week! Due back on " +
                             oneWeekFromNow.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            
            // Redirect to rental history
            return "rentalHistory.xhtml?faces-redirect=true";
            
        } catch (IllegalArgumentException e) {
            logger.warning("Quick rental failed: " + e.getMessage());
            addErrorMessage(e.getMessage());
            return null;
        } catch (Exception e) {
            logger.severe("Error during quick rental: " + e.getMessage());
            e.printStackTrace();
            addErrorMessage("An error occurred while processing your rental. Please try again.");
            return null;
        }
    }
    
    /**
     * Load user rental history
     */
    public void loadUserRentals() {
        try {
            if (sessionBean.isAuthenticated()) {
                userRentals = rentalService.getRentalsByUser(sessionBean.getCurrentUserId());
                filterRentals();
                logger.info("Loaded " + userRentals.size() + " rentals for user");
            } else {
                userRentals = new ArrayList<>();
            }
        } catch (Exception e) {
            logger.severe("Error loading user rentals: " + e.getMessage());
            userRentals = new ArrayList<>();
            addErrorMessage("Error loading rental history.");
        }
    }
    
    /**
     * Filter rentals by status
     */
    public void filterRentals() {
        if (userRentals == null) {
            filteredRentals = new ArrayList<>();
            return;
        }
        
        if ("ALL".equals(statusFilter)) {
            filteredRentals = new ArrayList<>(userRentals);
        } else {
            RentalStatus status = RentalStatus.valueOf(statusFilter);
            filteredRentals = userRentals.stream()
                .filter(rental -> rental.getStatus() == status)
                .toList();
        }
    }
    
    /**
     * Extend rental return date
     */
    public void extendRental(Long rentalId) {
        try {
            LocalDate newReturnDate = LocalDate.now().plusWeeks(1);
            boolean success = rentalService.extendRental(rentalId, newReturnDate);
            
            if (success) {
                addSuccessMessage("Rental extended successfully.");
                loadUserRentals();
            } else {
                addErrorMessage("Failed to extend rental.");
            }
        } catch (Exception e) {
            logger.severe("Error extending rental: " + e.getMessage());
            addErrorMessage("Error extending rental: " + e.getMessage());
        }
    }
    
    /**
     * Return a movie for regular users (user self-service)
     */
    public void userReturnMovie(Long rentalId) {
        try {
            // Check authentication
            if (!sessionBean.isAuthenticated()) {
                addErrorMessage("You must be logged in to return movies.");
                return;
            }
            
            // Find the rental with user and movie details eagerly loaded
            Optional<Rental> rentalOpt = rentalService.findRentalByIdWithDetails(rentalId);
            if (rentalOpt.isEmpty()) {
                addErrorMessage("Rental not found.");
                return;
            }
            
            Rental rental = rentalOpt.get();
            
            // Check if rental is active first
            if (rental.getStatus() != RentalStatus.ACTIVE) {
                addErrorMessage("This rental is not active and cannot be returned.");
                return;
            }
            
            // Validate that the rental belongs to the current user
            // Handle potential lazy loading issue with user relationship
            try {
                if (rental.getUser() == null || !rental.getUser().getUserId().equals(sessionBean.getCurrentUserId())) {
                    logger.warning("User " + sessionBean.getCurrentUserId() +
                                 " attempted to return rental " + rentalId +
                                 " - access denied or user relationship not loaded properly");
                    addErrorMessage("Access denied. You can only return your own rentals.");
                    return;
                }
            } catch (Exception userLoadException) {
                // If we can't load the user due to lazy loading, use alternative validation
                logger.warning("Could not validate user ownership due to lazy loading: " + userLoadException.getMessage());
                
                // Alternative validation: check if the rental ID exists in user's current rentals
                boolean isUserRental = userRentals != null && userRentals.stream()
                    .anyMatch(r -> r.getRentalId().equals(rentalId));
                
                if (!isUserRental) {
                    addErrorMessage("Access denied. You can only return your own rentals.");
                    return;
                }
            }
            
            // Process the return
            boolean success = rentalService.returnRental(rentalId);
            
            if (success) {
                // Get movie title safely
                String movieTitle = "the movie";
                try {
                    if (rental.getMovie() != null && rental.getMovie().getTitle() != null) {
                        movieTitle = "'" + rental.getMovie().getTitle() + "'";
                    }
                } catch (Exception e) {
                    // If movie can't be loaded, use generic message
                    logger.warning("Could not load movie title: " + e.getMessage());
                }
                
                addSuccessMessage("Movie " + movieTitle + " returned successfully.");
                
                // Force refresh the rental lists to update the UI
                loadUserRentals();
                filterRentals(); // Also refresh the filtered list
                
                logger.info("Rental return successful, lists refreshed");
            } else {
                addErrorMessage("Failed to process movie return. Please try again.");
            }
            
        } catch (Exception e) {
            logger.severe("Error returning movie for user: " + e.getMessage());
            e.printStackTrace();
            addErrorMessage("An error occurred while processing the return: " + e.getMessage());
        }
    }
    
    /**
     * Return a movie (admin function)
     */
    public void returnMovie(Long rentalId) {
        try {
            // Check admin access
            if (!sessionBean.isAdmin()) {
                addErrorMessage("Access denied. Admin privileges required.");
                return;
            }
            
            boolean success = rentalService.returnRental(rentalId);
            
            if (success) {
                addSuccessMessage("Movie returned successfully.");
                loadAllRentals(); // Reload admin data
            } else {
                addErrorMessage("Failed to process return.");
            }
        } catch (Exception e) {
            logger.severe("Error returning movie: " + e.getMessage());
            addErrorMessage("Error processing return: " + e.getMessage());
        }
    }
    
    /**
     * Load all rentals for admin view
     */
    public void loadAllRentals() {
        try {
            if (sessionBean.isAdmin()) {
                allRentals = rentalService.getAllRentals();
                logger.info("Loaded " + allRentals.size() + " total rentals for admin");
            }
        } catch (Exception e) {
            logger.severe("Error loading all rentals: " + e.getMessage());
            allRentals = new ArrayList<>();
            addErrorMessage("Error loading rental data.");
        }
    }
    
    /**
     * Load overdue rentals for admin view
     */
    public void loadOverdueRentals() {
        try {
            if (sessionBean.isAdmin()) {
                overdueRentals = rentalService.getOverdueRentals();
                logger.info("Loaded " + overdueRentals.size() + " overdue rentals");
            }
        } catch (Exception e) {
            logger.severe("Error loading overdue rentals: " + e.getMessage());
            overdueRentals = new ArrayList<>();
            addErrorMessage("Error loading overdue rental data.");
        }
    }
    
    /**
     * Check if user can rent the selected movie
     */
    public boolean canRentSelectedMovie() {
        if (selectedMovie == null || !sessionBean.isAuthenticated()) {
            return false;
        }
        
        return rentalService.canUserRentMovie(
            sessionBean.getCurrentUserId(), 
            selectedMovie.getMovieId()
        );
    }
    
    /**
     * Get formatted rental period
     */
    public String getRentalPeriod() {
        if (returnDate == null) {
            return "";
        }
        
        LocalDate today = LocalDate.now();
        long days = today.until(returnDate).getDays();
        
        if (days == 1) {
            return "1 day";
        } else if (days == 7) {
            return "1 week";
        } else if (days == 14) {
            return "2 weeks";
        } else {
            return days + " days";
        }
    }
    
    /**
     * Get status badge class for rental
     */
    public String getStatusClass(Rental rental) {
        switch (rental.getStatus()) {
            case ACTIVE:
                return rental.isOverdue() ? "status-overdue" : "status-active";
            case RETURNED:
                return "status-returned";
            case CANCELLED:
                return "status-cancelled";
            default:
                return "status-unknown";
        }
    }
    
    /**
     * Navigate to movie details page
     */
    public String viewMovieDetails(Long movieId) {
        logger.info("Navigating to movie details for movieId: " + movieId);
        return "movie.xhtml?faces-redirect=true&movieId=" + movieId;
    }
    
    /**
     * Get formatted return date
     */
    public String getFormattedReturnDate() {
        if (returnDate == null) {
            return "";
        }
        return returnDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }
    
    /**
     * Clear the rental form
     */
    private void clearForm() {
        movieId = null;
        selectedMovie = null;
        returnDate = LocalDate.now().plusWeeks(1);
        notes = "";
    }
    
    /**
     * Add success message
     */
    private void addSuccessMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
    }
    
    /**
     * Add error message
     */
    private void addErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }
    
    // Getters and Setters
    public Long getMovieId() {
        return movieId;
    }
    
    public void setMovieId(Long movieId) {
        this.movieId = movieId;
        if (movieId != null) {
            loadSelectedMovie();
        }
    }
    
    public Movie getSelectedMovie() {
        return selectedMovie;
    }
    
    public LocalDate getReturnDate() {
        return returnDate;
    }
    
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public List<Rental> getUserRentals() {
        return userRentals;
    }
    
    public List<Rental> getFilteredRentals() {
        return filteredRentals;
    }
    
    public String getStatusFilter() {
        return statusFilter;
    }
    
    public void setStatusFilter(String statusFilter) {
        this.statusFilter = statusFilter;
        filterRentals();
    }
    
    public List<Rental> getAllRentals() {
        return allRentals;
    }
    
    public List<Rental> getOverdueRentals() {
        return overdueRentals;
    }
    
    public int getUserRentalCount() {
        return userRentals != null ? userRentals.size() : 0;
    }
    
    public int getActiveRentalCount() {
        if (userRentals == null) return 0;
        return (int) userRentals.stream()
            .filter(rental -> rental.getStatus() == RentalStatus.ACTIVE)
            .count();
    }
}