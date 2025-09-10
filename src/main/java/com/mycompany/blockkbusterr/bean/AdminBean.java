package com.mycompany.blockkbusterr.bean;

import com.mycompany.blockkbusterr.entity.Movie;
import com.mycompany.blockkbusterr.entity.Rental;
import com.mycompany.blockkbusterr.entity.User;
import com.mycompany.blockkbusterr.service.MovieService;
import com.mycompany.blockkbusterr.service.RentalService;
import com.mycompany.blockkbusterr.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * JSF Managed Bean for admin dashboard functionality
 */
@Named("adminBean")
@RequestScoped
public class AdminBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(AdminBean.class.getName());
    
    @Inject
    private RentalService rentalService;
    
    @Inject
    private MovieService movieService;
    
    @Inject
    private UserService userService;
    
    @Inject
    private SessionBean sessionBean;
    
    // Dashboard data
    private List<Rental> recentRentals;
    private List<Rental> overdueRentals;
    private List<Movie> lowStockMovies;
    private List<Movie> allMoviesForStock; // All movies for Movie Stock section
    private List<User> users;
    private List<User> filteredUsers;
    
    // Statistics
    private RentalService.RentalStats rentalStats;
    private MovieService.MovieStats movieStats;
    private UserService.UserStats userStats;
    
    // User management
    private String userSearchTerm = "";
    private Long selectedUserId;
    private User selectedUser;
    
    // Movie management
    private List<Movie> allMovies;
    private String movieSearchTerm = "";
    
    @PostConstruct
    public void init() {
        try {
            // Check admin access
            if (!sessionBean.isAdmin()) {
                FacesContext.getCurrentInstance().getExternalContext()
                    .redirect("mainPage.xhtml?faces-redirect=true");
                return;
            }
            
            // Check if we're returning from movie management (refresh parameter)
            String refreshParam = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap().get("refresh");
            
            if ("true".equals(refreshParam)) {
                logger.info("DEBUG: Refresh parameter detected, forcing full data reload");
                // Force a complete refresh by explicitly clearing any caches
                lowStockMovies = null;
                allMovies = null;
            } else {
                logger.info("DEBUG: No refresh parameter detected, normal initialization");
            }
            
            loadDashboardData();
            
        } catch (Exception e) {
            logger.severe("Error initializing AdminBean: " + e.getMessage());
            addErrorMessage("Error loading admin dashboard data.");
        }
    }
    
    /**
     * Load all dashboard data
     */
    public void loadDashboardData() {
        try {
            loadRecentRentals();
            loadOverdueRentals();
            loadLowStockMovies();
            loadAllMoviesForStock(); // Load all movies for Movie Stock section
            loadStats();
            loadUsers();
            loadAllMovies();
            
        } catch (Exception e) {
            logger.severe("Error loading dashboard data: " + e.getMessage());
            addErrorMessage("Error loading dashboard data.");
        }
    }
    
    /**
     * Load recent rentals for dashboard
     */
    private void loadRecentRentals() {
        try {
            recentRentals = rentalService.getRecentRentals(30); // Last 30 days
            logger.info("Loaded " + recentRentals.size() + " recent rentals");
        } catch (Exception e) {
            logger.severe("Error loading recent rentals: " + e.getMessage());
            recentRentals = new ArrayList<>();
        }
    }
    
    /**
     * Load overdue rentals
     */
    private void loadOverdueRentals() {
        try {
            overdueRentals = rentalService.getOverdueRentals();
            logger.info("Loaded " + overdueRentals.size() + " overdue rentals");
        } catch (Exception e) {
            logger.severe("Error loading overdue rentals: " + e.getMessage());
            overdueRentals = new ArrayList<>();
        }
    }
    
    /**
     * Load low stock movies
     */
    private void loadLowStockMovies() {
        try {
            logger.info("DEBUG: Loading low stock movies with threshold=3");
            lowStockMovies = movieService.getLowStockMovies(3); // 3 or fewer copies
            logger.info("DEBUG: Loaded " + lowStockMovies.size() + " low stock movies");
            
            // Debug: log each movie found
            for (Movie movie : lowStockMovies) {
                logger.info("DEBUG: Low stock movie: " + movie.getTitle() + " (Quantity: " + movie.getQuantity() + ")");
            }
        } catch (Exception e) {
            logger.severe("Error loading low stock movies: " + e.getMessage());
            e.printStackTrace();
            lowStockMovies = new ArrayList<>();
        }
    }
    
    /**
     * Load low stock movies with forced refresh (for refresh button)
     */
    private void loadLowStockMoviesForceRefresh() {
        try {
            logger.info("DEBUG: Force loading low stock movies with threshold=3");
            lowStockMovies = movieService.getLowStockMoviesRefresh(3); // 3 or fewer copies with cache clear
            logger.info("DEBUG: Force loaded " + lowStockMovies.size() + " low stock movies");
            
            // Debug: log each movie found
            for (Movie movie : lowStockMovies) {
                logger.info("DEBUG: Force refresh - Low stock movie: " + movie.getTitle() + " (Quantity: " + movie.getQuantity() + ")");
            }
        } catch (Exception e) {
            logger.severe("Error force loading low stock movies: " + e.getMessage());
            e.printStackTrace();
            lowStockMovies = new ArrayList<>();
        }
    }
    
    /**
     * Load all movies for Movie Stock section (admin management view)
     */
    private void loadAllMoviesForStock() {
        try {
            logger.info("DEBUG: Loading all movies for Movie Stock section");
            allMoviesForStock = movieService.getAllMovies();
            logger.info("DEBUG: Loaded " + allMoviesForStock.size() + " movies for Movie Stock");
            
            // Debug: log each movie found
            for (Movie movie : allMoviesForStock) {
                logger.info("DEBUG: Movie Stock - Movie: " + movie.getTitle() + " (Quantity: " + movie.getQuantity() + ")");
            }
        } catch (Exception e) {
            logger.severe("Error loading all movies for stock: " + e.getMessage());
            e.printStackTrace();
            allMoviesForStock = new ArrayList<>();
        }
    }
    
    /**
     * Load all movies for Movie Stock section with forced refresh
     */
    private void loadAllMoviesForStockForceRefresh() {
        try {
            logger.info("DEBUG: Force loading all movies for Movie Stock section");
            // Use the existing getAllMovies method but clear cache first
            movieService.clearMovieCache(); // We'll add this method
            allMoviesForStock = movieService.getAllMovies();
            logger.info("DEBUG: Force loaded " + allMoviesForStock.size() + " movies for Movie Stock");
            
            // Debug: log each movie found
            for (Movie movie : allMoviesForStock) {
                logger.info("DEBUG: Force refresh - Movie Stock - Movie: " + movie.getTitle() + " (Quantity: " + movie.getQuantity() + ")");
            }
        } catch (Exception e) {
            logger.severe("Error force loading all movies for stock: " + e.getMessage());
            e.printStackTrace();
            allMoviesForStock = new ArrayList<>();
        }
    }
    
    /**
     * Load system statistics
     */
    private void loadStats() {
        try {
            rentalStats = rentalService.getRentalStats();
            movieStats = movieService.getMovieStats();
            userStats = userService.getUserStats();
            logger.info("Loaded system statistics");
        } catch (Exception e) {
            logger.severe("Error loading stats: " + e.getMessage());
        }
    }
    
    /**
     * Refresh movie stock data specifically (for AJAX calls)
     */
    public void refreshMovieStock() {
        logger.info("DEBUG: AdminBean.refreshMovieStock() called");
        try {
            // Clear any cached data at bean level
            lowStockMovies = null;
            allMovies = null;
            allMoviesForStock = null;
            movieStats = null;
            
            // Force complete reload with cache clearing
            loadLowStockMoviesForceRefresh();
            loadAllMoviesForStockForceRefresh(); // Refresh the Movie Stock section
            loadStats();
            loadAllMovies();
            
            addSuccessMessage("Movie stock data refreshed successfully.");
            logger.info("DEBUG: Movie stock refreshed successfully - found " + (lowStockMovies != null ? lowStockMovies.size() : 0) + " low stock movies");
        } catch (Exception e) {
            logger.severe("Error refreshing movie stock: " + e.getMessage());
            e.printStackTrace();
            addErrorMessage("Error refreshing movie stock data: " + e.getMessage());
        }
    }
    
    /**
     * Load all users for management
     */
    private void loadUsers() {
        try {
            users = userService.getAllUsers();
            filteredUsers = new ArrayList<>(users);
            logger.info("Loaded " + users.size() + " users");
        } catch (Exception e) {
            logger.severe("Error loading users: " + e.getMessage());
            users = new ArrayList<>();
            filteredUsers = new ArrayList<>();
        }
    }
    
    /**
     * Load all movies for management
     */
    private void loadAllMovies() {
        try {
            allMovies = movieService.getAllMovies();
            logger.info("Loaded " + allMovies.size() + " movies");
        } catch (Exception e) {
            logger.severe("Error loading movies: " + e.getMessage());
            allMovies = new ArrayList<>();
        }
    }
    
    /**
     * Search users by name
     */
    public void searchUsers() {
        try {
            if (userSearchTerm == null || userSearchTerm.trim().isEmpty()) {
                filteredUsers = new ArrayList<>(users);
            } else {
                filteredUsers = userService.searchUsersByName(userSearchTerm.trim());
            }
            logger.info("User search returned " + filteredUsers.size() + " results");
        } catch (Exception e) {
            logger.severe("Error searching users: " + e.getMessage());
            addErrorMessage("Error searching users.");
        }
    }
    
    /**
     * Search users by name - Ajax listener version
     */
    public void searchUsers(jakarta.faces.event.AjaxBehaviorEvent event) {
        searchUsers();
    }
    
    /**
     * Process return for a rental
     */
    public void processReturn(Long rentalId) {
        try {
            boolean success = rentalService.returnRental(rentalId);
            
            if (success) {
                addSuccessMessage("Movie returned successfully.");
                loadDashboardData(); // Refresh data
            } else {
                addErrorMessage("Failed to process return.");
            }
        } catch (Exception e) {
            logger.severe("Error processing return: " + e.getMessage());
            addErrorMessage("Error processing return: " + e.getMessage());
        }
    }
    
    /**
     * Update movie quantity
     */
    public void updateMovieQuantity(Long movieId, int newQuantity) {
        try {
            boolean success = movieService.updateMovieQuantity(movieId, newQuantity);
            
            if (success) {
                addSuccessMessage("Movie quantity updated successfully.");
                // Refresh all movie-related data
                loadLowStockMovies();
                loadAllMovies();
                loadStats(); // Update total movie count
            } else {
                addErrorMessage("Failed to update movie quantity.");
            }
        } catch (Exception e) {
            logger.severe("Error updating movie quantity: " + e.getMessage());
            addErrorMessage("Error updating movie quantity: " + e.getMessage());
        }
    }
    
    // User deactivation functionality removed as per requirements
    
    /**
     * Get user rental history
     */
    public List<Rental> getUserRentalHistory(Long userId) {
        try {
            return rentalService.getRentalsByUser(userId);
        } catch (Exception e) {
            logger.severe("Error getting user rental history: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Navigate to edit movie page
     */
    public String editMovie(Long movieId) {
        return "editMovie.xhtml?faces-redirect=true&movieId=" + movieId;
    }
    
    /**
     * Navigate to add movie page
     */
    public String addMovie() {
        return "addMovie.xhtml?faces-redirect=true";
    }
    
    /**
     * Delete a movie
     */
    public void deleteMovie(Long movieId) {
        try {
            logger.info("Attempting to delete movie with ID: " + movieId);
            boolean success = movieService.deleteMovie(movieId);
            
            if (success) {
                addSuccessMessage("Movie deleted successfully.");
                logger.info("Movie deleted successfully: " + movieId);
                // Refresh all data to ensure UI is updated
                loadLowStockMovies();
                loadAllMovies();
                loadStats();
            } else {
                addErrorMessage("Failed to delete movie. Movie may have active rentals or does not exist.");
                logger.warning("Failed to delete movie: " + movieId);
            }
        } catch (Exception e) {
            logger.severe("Error deleting movie: " + e.getMessage());
            addErrorMessage("Error deleting movie: " + e.getMessage());
        }
    }
    
    /**
     * Get rental status class for styling
     */
    public String getRentalStatusClass(Rental rental) {
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
     * Get stock level class for styling
     */
    public String getStockLevelClass(Movie movie) {
        if (movie.getQuantity() == 0) {
            return "stock-out";
        } else if (movie.getQuantity() <= 2) {
            return "stock-low";
        } else if (movie.getQuantity() <= 5) {
            return "stock-medium";
        } else {
            return "stock-good";
        }
    }
    
    /**
     * Get user status class for styling
     */
    public String getUserStatusClass(User user) {
        return user.getActive() ? "user-active" : "user-inactive";
    }
    
    /**
     * Format rental duration for display
     */
    public String getRentalDuration(Rental rental) {
        long days = rental.getDaysRented();
        if (days == 1) {
            return "1 day";
        } else {
            return days + " days";
        }
    }
    
    /**
     * Check if rental is overdue
     */
    public boolean isRentalOverdue(Rental rental) {
        return rental.isOverdue();
    }
    
    /**
     * Get overdue days for rental
     */
    public long getOverdueDays(Rental rental) {
        return rental.getDaysOverdue();
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
    public List<Rental> getRecentRentals() {
        return recentRentals;
    }
    
    public List<Rental> getOverdueRentals() {
        return overdueRentals;
    }
    
    public List<Movie> getLowStockMovies() {
        return lowStockMovies;
    }
    
    public List<Movie> getAllMoviesForStock() {
        return allMoviesForStock;
    }
    
    public List<User> getUsers() {
        return users;
    }
    
    public List<User> getFilteredUsers() {
        return filteredUsers;
    }
    
    public RentalService.RentalStats getRentalStats() {
        return rentalStats;
    }
    
    public MovieService.MovieStats getMovieStats() {
        return movieStats;
    }
    
    public UserService.UserStats getUserStats() {
        return userStats;
    }
    
    public String getUserSearchTerm() {
        return userSearchTerm;
    }
    
    public void setUserSearchTerm(String userSearchTerm) {
        this.userSearchTerm = userSearchTerm;
    }
    
    public Long getSelectedUserId() {
        return selectedUserId;
    }
    
    public void setSelectedUserId(Long selectedUserId) {
        this.selectedUserId = selectedUserId;
    }
    
    public User getSelectedUser() {
        return selectedUser;
    }
    
    public List<Movie> getAllMovies() {
        return allMovies;
    }
    
    public String getMovieSearchTerm() {
        return movieSearchTerm;
    }
    
    public void setMovieSearchTerm(String movieSearchTerm) {
        this.movieSearchTerm = movieSearchTerm;
    }
    
    // Statistics getters for convenience
    public long getTotalRentals() {
        return rentalStats != null ? rentalStats.getTotalRentals() : 0;
    }
    
    public long getActiveRentals() {
        return rentalStats != null ? rentalStats.getActiveRentals() : 0;
    }
    
    public long getTotalMovies() {
        return movieStats != null ? movieStats.getTotalMovies() : 0;
    }
    
    public long getAvailableMovies() {
        return movieStats != null ? movieStats.getAvailableMovies() : 0;
    }
    
    public long getTotalUsers() {
        return userStats != null ? userStats.getTotalUsers() : 0;
    }
    
    public long getActiveUsers() {
        return userStats != null ? userStats.getActiveUsers() : 0;
    }
    
    public int getOverdueRentalCount() {
        return overdueRentals != null ? overdueRentals.size() : 0;
    }
    
    public int getLowStockMovieCount() {
        return lowStockMovies != null ? lowStockMovies.size() : 0;
    }
    
    /**
     * Navigate to main page
     */
    public String goToMainPage() {
        return "mainPage.xhtml?faces-redirect=true";
    }
    
    /**
     * Safely format date for display - prevents conversion errors
     */
    public String formatDate(java.time.LocalDate date) {
        if (date == null) {
            return "N/A";
        }
        try {
            return date.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        } catch (Exception e) {
            logger.warning("Error formatting date: " + e.getMessage());
            return "Invalid Date";
        }
    }
    
    /**
     * Safely format date for display - prevents conversion errors
     */
    public String formatDate(java.time.LocalDateTime dateTime) {
        if (dateTime == null) {
            return "N/A";
        }
        try {
            return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
        } catch (Exception e) {
            logger.warning("Error formatting datetime: " + e.getMessage());
            return "Invalid Date";
        }
    }
}