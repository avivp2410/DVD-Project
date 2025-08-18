package com.mycompany.blockkbusterr.bean;

import com.mycompany.blockkbusterr.entity.Movie;
import com.mycompany.blockkbusterr.entity.Rental;
import com.mycompany.blockkbusterr.entity.RentalStatus;
import com.mycompany.blockkbusterr.service.MovieService;
import com.mycompany.blockkbusterr.service.RentalService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
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
@RequestScoped
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
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9+\\-\\s()]+$", message = "Please enter a valid phone number")
    private String phoneNumber;
    
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
            // Get movie ID from URL parameter if present
            String movieIdParam = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap().get("movieId");
            
            if (movieIdParam != null && !movieIdParam.isEmpty()) {
                try {
                    movieId = Long.parseLong(movieIdParam);
                    loadSelectedMovie();
                } catch (NumberFormatException e) {
                    logger.warning("Invalid movie ID parameter: " + movieIdParam);
                    addErrorMessage("Invalid movie selection.");
                }
            }
            
            // Set default return date (1 week from now)
            returnDate = LocalDate.now().plusWeeks(1);
            
            // Load user rentals if authenticated
            if (sessionBean.isAuthenticated()) {
                loadUserRentals();
            }
            
        } catch (Exception e) {
            logger.severe("Error initializing RentalBean: " + e.getMessage());
            addErrorMessage("An error occurred while loading rental information.");
        }
    }
    
    /**
     * Load the selected movie for rental
     */
    private void loadSelectedMovie() {
        if (movieId != null) {
            Optional<Movie> movieOpt = movieService.findMovieById(movieId);
            if (movieOpt.isPresent()) {
                selectedMovie = movieOpt.get();
                logger.info("Loaded movie for rental: " + selectedMovie.getTitle());
            } else {
                addErrorMessage("Movie not found.");
                logger.warning("Movie not found with ID: " + movieId);
            }
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
                returnDate,
                phoneNumber
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
        phoneNumber = "";
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
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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