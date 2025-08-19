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
        if (!movieRepository.decreaseQuantity(movieId)) {
            throw new RuntimeException("Failed to update movie quantity");
        }
        
        return rentalRepository.save(rental);
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
     * Get rental history for a user
     */
    public List<Rental> getRentalHistory(Long userId, int limit) {
        List<Rental> rentals = rentalRepository.findByUserId(userId);
        return rentals.stream().limit(limit).toList();
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
        long returnedRentals = rentalRepository.countByStatus(RentalStatus.RETURNED);
        long overdueRentals = rentalRepository.countOverdueRentals();
        
        return new RentalStats(totalRentals, activeRentals, returnedRentals, overdueRentals);
    }
    
    /**
     * Get user rental statistics
     */
    public UserRentalStats getUserRentalStats(Long userId) {
        Object[] stats = rentalRepository.getUserRentalStats(userId);
        
        long totalRentals = ((Number) stats[0]).longValue();
        long activeRentals = ((Number) stats[1]).longValue();
        long returnedRentals = ((Number) stats[2]).longValue();
        long overdueRentals = ((Number) stats[3]).longValue();
        
        return new UserRentalStats(totalRentals, activeRentals, returnedRentals, overdueRentals);
    }
    
    /**
     * Process overdue rentals (mark as overdue)
     */
    public int processOverdueRentals() {
        List<Rental> overdueRentals = rentalRepository.findOverdueRentals();
        int processedCount = 0;
        
        for (Rental rental : overdueRentals) {
            if (rental.getStatus() == RentalStatus.ACTIVE) {
                rental.setStatus(RentalStatus.OVERDUE);
                rentalRepository.update(rental);
                processedCount++;
            }
        }
        
        return processedCount;
    }
    
    // Inner class for rental statistics
    public static class RentalStats {
        private final long totalRentals;
        private final long activeRentals;
        private final long returnedRentals;
        private final long overdueRentals;
        
        public RentalStats(long totalRentals, long activeRentals, long returnedRentals, long overdueRentals) {
            this.totalRentals = totalRentals;
            this.activeRentals = activeRentals;
            this.returnedRentals = returnedRentals;
            this.overdueRentals = overdueRentals;
        }
        
        public long getTotalRentals() { return totalRentals; }
        public long getActiveRentals() { return activeRentals; }
        public long getReturnedRentals() { return returnedRentals; }
        public long getOverdueRentals() { return overdueRentals; }
        public long getCancelledRentals() { return totalRentals - activeRentals - returnedRentals - overdueRentals; }
    }
    
    // Inner class for user rental statistics
    public static class UserRentalStats {
        private final long totalRentals;
        private final long activeRentals;
        private final long returnedRentals;
        private final long overdueRentals;
        
        public UserRentalStats(long totalRentals, long activeRentals, long returnedRentals, long overdueRentals) {
            this.totalRentals = totalRentals;
            this.activeRentals = activeRentals;
            this.returnedRentals = returnedRentals;
            this.overdueRentals = overdueRentals;
        }
        
        public long getTotalRentals() { return totalRentals; }
        public long getActiveRentals() { return activeRentals; }
        public long getReturnedRentals() { return returnedRentals; }
        public long getOverdueRentals() { return overdueRentals; }
    }
}