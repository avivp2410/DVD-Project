package com.mycompany.blockkbusterr.service;

import com.mycompany.blockkbusterr.entity.Movie;
import com.mycompany.blockkbusterr.entity.Rental;
import com.mycompany.blockkbusterr.entity.RentalStatus;
import com.mycompany.blockkbusterr.entity.User;
import com.mycompany.blockkbusterr.repository.MovieRepository;
import com.mycompany.blockkbusterr.repository.RentalRepository;
import com.mycompany.blockkbusterr.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class RentalService {
    
    @Inject
    private RentalRepository rentalRepository;
    
    @Inject
    private UserRepository userRepository;
    
    @Inject
    private MovieRepository movieRepository;
    
    /**
     * Create a new rental
     */
    public Rental createRental(Long userId, Long movieId, LocalDate returnDate) {
        // Validate user
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        User user = userOpt.get();
        
        if (!user.getActive()) {
            throw new IllegalArgumentException("User account is not active");
        }
        
        // Validate movie
        Optional<Movie> movieOpt = movieRepository.findById(movieId);
        if (movieOpt.isEmpty()) {
            throw new IllegalArgumentException("Movie not found");
        }
        Movie movie = movieOpt.get();
        
        if (!movie.getActive()) {
            throw new IllegalArgumentException("Movie is not active");
        }
        
        if (!movie.isAvailable()) {
            throw new IllegalArgumentException("Movie is not available for rental");
        }
        
        // Validate return date
        if (returnDate == null) {
            throw new IllegalArgumentException("Return date is required");
        }
        
        if (returnDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Return date cannot be in the past");
        }
        
        // Check if user already has an active rental for this movie
        if (rentalRepository.hasActiveRental(userId, movieId)) {
            throw new IllegalArgumentException("User already has an active rental for this movie");
        }
        
        // Check rental limits (optional business rule)
        long activeRentals = rentalRepository.countActiveRentalsByUser(userId);
        if (activeRentals >= 5) { // Max 5 active rentals per user
            throw new IllegalArgumentException("User has reached maximum number of active rentals");
        }
        
        // Create rental
        Rental rental = new Rental();
        rental.setUser(user);
        rental.setMovie(movie);
        rental.setBorrowDate(LocalDate.now());
        rental.setReturnDate(returnDate);
        rental.setStatus(RentalStatus.ACTIVE);
        
        // Decrease movie quantity
        System.out.println("DEBUG: Attempting to decrease quantity for movie ID: " + movieId + " from " + movie.getQuantity());
        if (!movieRepository.decreaseQuantity(movieId)) {
            System.err.println("ERROR: Failed to decrease quantity for movie ID: " + movieId);
            throw new RuntimeException("Failed to update movie quantity");
        }
        System.out.println("DEBUG: Successfully decreased quantity for movie ID: " + movieId);
        
        Rental savedRental = rentalRepository.save(rental);
        System.out.println("DEBUG: Rental created successfully with ID: " + savedRental.getRentalId());
        return savedRental;
    }
    
    /**
     * Return a rental
     */
    public boolean returnRental(Long rentalId) {
        Optional<Rental> rentalOpt = rentalRepository.findById(rentalId);
        if (rentalOpt.isEmpty()) {
            return false;
        }
        
        Rental rental = rentalOpt.get();
        
        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new IllegalArgumentException("Rental is not active");
        }
        
        // Mark rental as returned
        rental.markAsReturned();
        rentalRepository.update(rental);
        
        // Increase movie quantity
        movieRepository.increaseQuantity(rental.getMovie().getMovieId());
        
        return true;
    }
    
    /**
     * Find rental by ID
     */
    public Optional<Rental> findRentalById(Long rentalId) {
        return rentalRepository.findById(rentalId);
    }
    
    /**
     * Find rental by ID with user and movie eagerly loaded
     */
    public Optional<Rental> findRentalByIdWithDetails(Long rentalId) {
        return rentalRepository.findByIdWithDetails(rentalId);
    }
    
    /**
     * Get all rentals
     */
    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }
    
    /**
     * Get rentals by user
     */
    public List<Rental> getRentalsByUser(Long userId) {
        return rentalRepository.findByUserId(userId);
    }
    
    /**
     * Get rentals by movie
     */
    public List<Rental> getRentalsByMovie(Long movieId) {
        return rentalRepository.findByMovieId(movieId);
    }
    
    /**
     * Get active rentals
     */
    public List<Rental> getActiveRentals() {
        return rentalRepository.findActiveRentals();
    }
    
    /**
     * Get active rentals by user
     */
    public List<Rental> getActiveRentalsByUser(Long userId) {
        return rentalRepository.findActiveRentalsByUserId(userId);
    }
    
    /**
     * Get overdue rentals
     */
    public List<Rental> getOverdueRentals() {
        return rentalRepository.findOverdueRentals();
    }
    
    /**
     * Get rentals by status
     */
    public List<Rental> getRentalsByStatus(RentalStatus status) {
        return rentalRepository.findByStatus(status);
    }
    
    /**
     * Get rentals by date range
     */
    public List<Rental> getRentalsByDateRange(LocalDate startDate, LocalDate endDate) {
        return rentalRepository.findByDateRange(startDate, endDate);
    }
    
    /**
     * Get rentals due on specific date
     */
    public List<Rental> getRentalsDueOnDate(LocalDate date) {
        return rentalRepository.findDueOnDate(date);
    }
    
    /**
     * Get rentals due within specified days
     */
    public List<Rental> getRentalsDueWithinDays(int days) {
        return rentalRepository.findDueWithinDays(days);
    }
    
    /**
     * Get recent rentals
     */
    public List<Rental> getRecentRentals(int days) {
        return rentalRepository.findRecentRentals(days);
    }
    
    /**
     * Check if user can rent movie
     */
    public boolean canUserRentMovie(Long userId, Long movieId) {
        // Check if user exists and is active
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty() || !userOpt.get().getActive()) {
            return false;
        }
        
        // Check if movie exists and is available
        Optional<Movie> movieOpt = movieRepository.findById(movieId);
        if (movieOpt.isEmpty() || !movieOpt.get().isAvailable()) {
            return false;
        }
        
        // Check if user already has an active rental for this movie
        if (rentalRepository.hasActiveRental(userId, movieId)) {
            return false;
        }
        
        // Check rental limits
        long activeRentals = rentalRepository.countActiveRentalsByUser(userId);
        return activeRentals < 5;
    }
    
    /**
     * Extend rental return date
     */
    public boolean extendRental(Long rentalId, LocalDate newReturnDate) {
        Optional<Rental> rentalOpt = rentalRepository.findById(rentalId);
        if (rentalOpt.isEmpty()) {
            return false;
        }
        
        Rental rental = rentalOpt.get();
        
        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new IllegalArgumentException("Rental is not active");
        }
        
        if (newReturnDate == null || newReturnDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("New return date must be in the future");
        }
        
        if (newReturnDate.isBefore(rental.getReturnDate())) {
            throw new IllegalArgumentException("New return date cannot be earlier than current return date");
        }
        
        rental.setReturnDate(newReturnDate);
        rentalRepository.update(rental);
        return true;
    }
    
    /**
     * Cancel rental
     */
    public boolean cancelRental(Long rentalId) {
        Optional<Rental> rentalOpt = rentalRepository.findById(rentalId);
        if (rentalOpt.isEmpty()) {
            return false;
        }
        
        Rental rental = rentalOpt.get();
        
        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new IllegalArgumentException("Rental is not active");
        }
        
        rental.setStatus(RentalStatus.CANCELLED);
        rentalRepository.update(rental);
        
        // Increase movie quantity back
        movieRepository.increaseQuantity(rental.getMovie().getMovieId());
        
        return true;
    }
    
    
    /**
     * Get rental history for a movie
     */
    public List<Rental> getMovieRentalHistory(Long movieId, int limit) {
        return rentalRepository.getRentalHistoryByMovie(movieId, limit);
    }
    
    /**
     * Get rental statistics
     */
    public RentalStats getRentalStats() {
        long totalRentals = rentalRepository.count();
        long activeRentals = rentalRepository.countByStatus(RentalStatus.ACTIVE);
        long overdueRentals = rentalRepository.countOverdueRentals();
        long returnedRentals = rentalRepository.countByStatus(RentalStatus.RETURNED);
        
        return new RentalStats(totalRentals, activeRentals, overdueRentals, returnedRentals);
    }
    
    // Inner class for rental statistics
    public static class RentalStats {
        private final long totalRentals;
        private final long activeRentals;
        private final long overdueRentals;
        private final long returnedRentals;
        
        public RentalStats(long totalRentals, long activeRentals, long overdueRentals, long returnedRentals) {
            this.totalRentals = totalRentals;
            this.activeRentals = activeRentals;
            this.overdueRentals = overdueRentals;
            this.returnedRentals = returnedRentals;
        }
        
        public long getTotalRentals() { return totalRentals; }
        public long getActiveRentals() { return activeRentals; }
        public long getOverdueRentals() { return overdueRentals; }
        public long getReturnedRentals() { return returnedRentals; }
    }
}