package com.mycompany.blockkbusterr.repository;

import com.mycompany.blockkbusterr.entity.Movie;
import com.mycompany.blockkbusterr.entity.Rental;
import com.mycompany.blockkbusterr.entity.RentalStatus;
import com.mycompany.blockkbusterr.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RentalRepository extends BaseRepository<Rental, Long> {
    
    public RentalRepository() {
        super(Rental.class);
    }
    
    @Override
    protected Long getId(Rental entity) {
        return entity.getRentalId();
    }
    
    /**
     * Find rentals by user
     */
    public List<Rental> findByUser(User user) {
        TypedQuery<Rental> query = createNamedQuery("Rental.findByUser");
        query.setParameter("user", user);
        return query.getResultList();
    }
    
    /**
     * Find rentals by user ID with movie and user details eagerly loaded
     */
    public List<Rental> findByUserId(Long userId) {
        String jpql = "SELECT r FROM Rental r " +
                     "LEFT JOIN FETCH r.user " +
                     "LEFT JOIN FETCH r.movie " +
                     "WHERE r.user.userId = :userId ORDER BY r.borrowDate DESC";
        TypedQuery<Rental> query = entityManager.createQuery(jpql, Rental.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
    
    /**
     * Find rentals by movie
     */
    public List<Rental> findByMovie(Movie movie) {
        TypedQuery<Rental> query = createNamedQuery("Rental.findByMovie");
        query.setParameter("movie", movie);
        return query.getResultList();
    }
    
    /**
     * Find rentals by movie ID
     */
    public List<Rental> findByMovieId(Long movieId) {
        String jpql = "SELECT r FROM Rental r WHERE r.movie.movieId = :movieId ORDER BY r.borrowDate DESC";
        TypedQuery<Rental> query = entityManager.createQuery(jpql, Rental.class);
        query.setParameter("movieId", movieId);
        return query.getResultList();
    }
    
    /**
     * Find rentals by status
     */
    public List<Rental> findByStatus(RentalStatus status) {
        TypedQuery<Rental> query = createNamedQuery("Rental.findByStatus");
        query.setParameter("status", status);
        return query.getResultList();
    }
    
    /**
     * Find active rentals
     */
    public List<Rental> findActiveRentals() {
        TypedQuery<Rental> query = createNamedQuery("Rental.findActiveRentals");
        return query.getResultList();
    }
    
    /**
     * Find overdue rentals
     */
    public List<Rental> findOverdueRentals() {
        TypedQuery<Rental> query = createNamedQuery("Rental.findOverdueRentals");
        return query.getResultList();
    }
    
    /**
     * Find rentals by date range
     */
    public List<Rental> findByDateRange(LocalDate startDate, LocalDate endDate) {
        String jpql = "SELECT r FROM Rental r WHERE r.borrowDate BETWEEN :startDate AND :endDate ORDER BY r.borrowDate DESC";
        TypedQuery<Rental> query = entityManager.createQuery(jpql, Rental.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }
    
    /**
     * Find rentals due for return on specific date
     */
    public List<Rental> findDueOnDate(LocalDate date) {
        String jpql = "SELECT r FROM Rental r WHERE r.returnDate = :date AND r.status = :status ORDER BY r.borrowDate";
        TypedQuery<Rental> query = entityManager.createQuery(jpql, Rental.class);
        query.setParameter("date", date);
        query.setParameter("status", RentalStatus.ACTIVE);
        return query.getResultList();
    }
    
    /**
     * Find rentals due for return within specified days
     */
    public List<Rental> findDueWithinDays(int days) {
        LocalDate cutoffDate = LocalDate.now().plusDays(days);
        String jpql = "SELECT r FROM Rental r WHERE r.returnDate <= :cutoffDate AND r.status = :status ORDER BY r.returnDate";
        TypedQuery<Rental> query = entityManager.createQuery(jpql, Rental.class);
        query.setParameter("cutoffDate", cutoffDate);
        query.setParameter("status", RentalStatus.ACTIVE);
        return query.getResultList();
    }
    
    /**
     * Find recent rentals (last N days)
     */
    public List<Rental> findRecentRentals(int days) {
        LocalDate cutoffDate = LocalDate.now().minusDays(days);
        String jpql = "SELECT r FROM Rental r WHERE r.borrowDate >= :cutoffDate ORDER BY r.borrowDate DESC";
        TypedQuery<Rental> query = entityManager.createQuery(jpql, Rental.class);
        query.setParameter("cutoffDate", cutoffDate);
        return query.getResultList();
    }
    
    /**
     * Find user's active rentals
     */
    public List<Rental> findActiveRentalsByUser(User user) {
        String jpql = "SELECT r FROM Rental r WHERE r.user = :user AND r.status = :status ORDER BY r.returnDate ASC";
        TypedQuery<Rental> query = entityManager.createQuery(jpql, Rental.class);
        query.setParameter("user", user);
        query.setParameter("status", RentalStatus.ACTIVE);
        return query.getResultList();
    }
    
    /**
     * Find user's active rentals by user ID
     */
    public List<Rental> findActiveRentalsByUserId(Long userId) {
        String jpql = "SELECT r FROM Rental r WHERE r.user.userId = :userId AND r.status = :status ORDER BY r.returnDate ASC";
        TypedQuery<Rental> query = entityManager.createQuery(jpql, Rental.class);
        query.setParameter("userId", userId);
        query.setParameter("status", RentalStatus.ACTIVE);
        return query.getResultList();
    }
    
    /**
     * Check if user has active rental for specific movie
     */
    public boolean hasActiveRental(Long userId, Long movieId) {
        String jpql = "SELECT COUNT(r) FROM Rental r WHERE r.user.userId = :userId AND r.movie.movieId = :movieId AND r.status = :status";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("userId", userId);
        query.setParameter("movieId", movieId);
        query.setParameter("status", RentalStatus.ACTIVE);
        return query.getSingleResult() > 0;
    }
    
    /**
     * Count active rentals by user
     */
    public long countActiveRentalsByUser(Long userId) {
        String jpql = "SELECT COUNT(r) FROM Rental r WHERE r.user.userId = :userId AND r.status = :status";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("userId", userId);
        query.setParameter("status", RentalStatus.ACTIVE);
        return query.getSingleResult();
    }
    
    /**
     * Count rentals by status
     */
    public long countByStatus(RentalStatus status) {
        String jpql = "SELECT COUNT(r) FROM Rental r WHERE r.status = :status";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("status", status);
        return query.getSingleResult();
    }
    
    /**
     * Count overdue rentals
     */
    public long countOverdueRentals() {
        String jpql = "SELECT COUNT(r) FROM Rental r WHERE r.status = :status AND r.returnDate < :currentDate";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("status", RentalStatus.ACTIVE);
        query.setParameter("currentDate", LocalDate.now());
        return query.getSingleResult();
    }
    
    /**
     * Find rental statistics for a user
     */
    public Object[] getUserRentalStats(Long userId) {
        String jpql = "SELECT " +
                     "COUNT(r), " +
                     "SUM(CASE WHEN r.status = :active THEN 1 ELSE 0 END), " +
                     "SUM(CASE WHEN r.status = :returned THEN 1 ELSE 0 END), " +
                     "SUM(CASE WHEN r.status = :active AND r.returnDate < :currentDate THEN 1 ELSE 0 END) " +
                     "FROM Rental r WHERE r.user.userId = :userId";
        TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
        query.setParameter("userId", userId);
        query.setParameter("active", RentalStatus.ACTIVE);
        query.setParameter("returned", RentalStatus.RETURNED);
        query.setParameter("currentDate", LocalDate.now());
        return query.getSingleResult();
    }
    
    /**
     * Update rental status
     */
    public boolean updateRentalStatus(Long rentalId, RentalStatus status) {
        String jpql = "UPDATE Rental r SET r.status = :status WHERE r.rentalId = :rentalId";
        int updatedRows = entityManager.createQuery(jpql)
                .setParameter("status", status)
                .setParameter("rentalId", rentalId)
                .executeUpdate();
        return updatedRows > 0;
    }
    
    /**
     * Mark rental as returned
     */
    public boolean markAsReturned(Long rentalId) {
        String jpql = "UPDATE Rental r SET r.status = :status, r.actualReturnDate = :returnDate WHERE r.rentalId = :rentalId";
        int updatedRows = entityManager.createQuery(jpql)
                .setParameter("status", RentalStatus.RETURNED)
                .setParameter("returnDate", LocalDate.now())
                .setParameter("rentalId", rentalId)
                .executeUpdate();
        return updatedRows > 0;
    }
    
    /**
     * Get rental history for a movie
     */
    public List<Rental> getRentalHistoryByMovie(Long movieId, int limit) {
        String jpql = "SELECT r FROM Rental r WHERE r.movie.movieId = :movieId ORDER BY r.borrowDate DESC";
        TypedQuery<Rental> query = entityManager.createQuery(jpql, Rental.class);
        query.setParameter("movieId", movieId);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    /**
     * Find rental by ID with user and movie eagerly loaded
     */
    public Optional<Rental> findByIdWithDetails(Long rentalId) {
        try {
            String jpql = "SELECT r FROM Rental r " +
                         "LEFT JOIN FETCH r.user " +
                         "LEFT JOIN FETCH r.movie " +
                         "WHERE r.rentalId = :rentalId";
            
            List<Rental> results = entityManager.createQuery(jpql, Rental.class)
                .setParameter("rentalId", rentalId)
                .getResultList();
                
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (Exception e) {
            // Fallback to regular findById if FETCH query fails
            return findById(rentalId);
        }
    }
}