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
            lowStockMovies = movieService.getLowStockMovies(3); // 3 or fewer copies
            logger.info("Loaded " + lowStockMovies.size() + " low stock movies");
        } catch (Exception e) {
            logger.severe("Error loading low stock movies: " + e.getMessage());
            lowStockMovies = new ArrayList<>();
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
                loadLowStockMovies(); // Refresh low stock data
                loadAllMovies(); // Refresh movie data
            } else {
                addErrorMessage("Failed to update movie quantity.");
            }
        } catch (Exception e) {
            logger.severe("Error updating movie quantity: " + e.getMessage());
            addErrorMessage("Error updating movie quantity: " + e.getMessage());
        }
    }
    
    /**
     * Toggle user active status
     */
    public void toggleUserStatus(Long userId) {
        try {
            Optional<User> userOpt = userService.findUserById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                boolean success;
                
                if (user.getActive()) {
                    success = userService.deactivateUser(userId);
                    if (success) {
                        addSuccessMessage("User " + user.getUsername() + " deactivated.");
                    }
                } else {
                    success = userService.activateUser(userId);
                    if (success) {
                        addSuccessMessage("User " + user.getUsername() + " activated.");
                    }
                }
                
                if (success) {
                    loadUsers(); // Refresh user data
                } else {
                    addErrorMessage("Failed to update user status.");
                }
            }
        } catch (Exception e) {
            logger.severe("Error toggling user status: " + e.getMessage());
            addErrorMessage("Error updating user status: " + e.getMessage());
        }
    }
    
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