package com.mycompany.blockkbusterr.repository;

import com.mycompany.blockkbusterr.entity.Movie;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class MovieRepository extends BaseRepository<Movie, Long> {
    
    public MovieRepository() {
        super(Movie.class);
    }
    
    @Override
    protected Long getId(Movie entity) {
        return entity.getMovieId();
    }
    
    /**
     * Find movies by title (partial match)
     */
    public List<Movie> findByTitle(String title) {
        TypedQuery<Movie> query = createNamedQuery("Movie.findByTitle");
        query.setParameter("title", title);
        return query.getResultList();
    }
    
    /**
     * Find movies by genre (partial match)
     */
    public List<Movie> findByGenre(String genre) {
        TypedQuery<Movie> query = createNamedQuery("Movie.findByGenre");
        query.setParameter("genre", genre);
        return query.getResultList();
    }
    
    /**
     * Find available movies (quantity > 0)
     */
    public List<Movie> findAvailableMovies() {
        TypedQuery<Movie> query = createNamedQuery("Movie.findAvailable");
        return query.getResultList();
    }
    
    /**
     * Find movies by release year
     */
    public List<Movie> findByReleaseYear(int year) {
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);
        
        String jpql = "SELECT m FROM Movie m WHERE m.releaseDate BETWEEN :startDate AND :endDate ORDER BY m.title";
        TypedQuery<Movie> query = entityManager.createQuery(jpql, Movie.class);
        query.setParameter("startDate", startOfYear);
        query.setParameter("endDate", endOfYear);
        return query.getResultList();
    }
    
    /**
     * Find movies by release year range
     */
    public List<Movie> findByReleaseYearRange(int startYear, int endYear) {
        LocalDate startDate = LocalDate.of(startYear, 1, 1);
        LocalDate endDate = LocalDate.of(endYear, 12, 31);
        
        String jpql = "SELECT m FROM Movie m WHERE m.releaseDate BETWEEN :startDate AND :endDate ORDER BY m.releaseDate DESC";
        TypedQuery<Movie> query = entityManager.createQuery(jpql, Movie.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }
    
    /**
     * Find movies by duration range
     */
    public List<Movie> findByDurationRange(int minDuration, int maxDuration) {
        String jpql = "SELECT m FROM Movie m WHERE m.duration BETWEEN :minDuration AND :maxDuration ORDER BY m.title";
        TypedQuery<Movie> query = entityManager.createQuery(jpql, Movie.class);
        query.setParameter("minDuration", minDuration);
        query.setParameter("maxDuration", maxDuration);
        return query.getResultList();
    }
    
    /**
     * Search movies by multiple criteria
     */
    public List<Movie> searchMovies(String searchTerm) {
        String jpql = "SELECT m FROM Movie m WHERE " +
                     "LOWER(m.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "LOWER(m.genre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "LOWER(m.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
                     "ORDER BY m.title";
        TypedQuery<Movie> query = entityManager.createQuery(jpql, Movie.class);
        query.setParameter("searchTerm", searchTerm);
        return query.getResultList();
    }
    
    /**
     * Find movies with low stock
     */
    public List<Movie> findLowStockMovies(int threshold) {
        String jpql = "SELECT m FROM Movie m WHERE m.quantity <= :threshold AND m.quantity > 0 ORDER BY m.quantity ASC";
        TypedQuery<Movie> query = entityManager.createQuery(jpql, Movie.class);
        query.setParameter("threshold", threshold);
        return query.getResultList();
    }
    
    /**
     * Find out of stock movies
     */
    public List<Movie> findOutOfStockMovies() {
        String jpql = "SELECT m FROM Movie m WHERE m.quantity = 0 ORDER BY m.title";
        TypedQuery<Movie> query = entityManager.createQuery(jpql, Movie.class);
        return query.getResultList();
    }
    
    /**
     * Find newest movies
     */
    public List<Movie> findNewestMovies(int limit) {
        String jpql = "SELECT m FROM Movie m ORDER BY m.releaseDate DESC";
        TypedQuery<Movie> query = entityManager.createQuery(jpql, Movie.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    /**
     * Find most popular movies (based on rental count)
     */
    public List<Movie> findMostPopularMovies(int limit) {
        String jpql = "SELECT m FROM Movie m " +
                     "LEFT JOIN m.rentals r " +
                     "GROUP BY m " +
                     "ORDER BY COUNT(r) DESC";
        TypedQuery<Movie> query = entityManager.createQuery(jpql, Movie.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    /**
     * Find movies by average rating
     */
    public List<Movie> findByMinimumRating(double minRating) {
        String jpql = "SELECT m FROM Movie m " +
                     "WHERE (SELECT AVG(CAST(rev.rating AS double)) FROM Review rev WHERE rev.movie = m) >= :minRating " +
                     "ORDER BY (SELECT AVG(CAST(rev.rating AS double)) FROM Review rev WHERE rev.movie = m) DESC";
        TypedQuery<Movie> query = entityManager.createQuery(jpql, Movie.class);
        query.setParameter("minRating", minRating);
        return query.getResultList();
    }
    
    /**
     * Update movie quantity
     */
    public boolean updateQuantity(Long movieId, int newQuantity) {
        String jpql = "UPDATE Movie m SET m.quantity = :quantity WHERE m.movieId = :movieId";
        int updatedRows = entityManager.createQuery(jpql)
                .setParameter("quantity", newQuantity)
                .setParameter("movieId", movieId)
                .executeUpdate();
        return updatedRows > 0;
    }
    
    /**
     * Decrease movie quantity (for rentals)
     */
    public boolean decreaseQuantity(Long movieId) {
        String jpql = "UPDATE Movie m SET m.quantity = m.quantity - 1 WHERE m.movieId = :movieId AND m.quantity > 0";
        int updatedRows = entityManager.createQuery(jpql)
                .setParameter("movieId", movieId)
                .executeUpdate();
        return updatedRows > 0;
    }
    
    /**
     * Increase movie quantity (for returns)
     */
    public boolean increaseQuantity(Long movieId) {
        String jpql = "UPDATE Movie m SET m.quantity = m.quantity + 1 WHERE m.movieId = :movieId";
        int updatedRows = entityManager.createQuery(jpql)
                .setParameter("movieId", movieId)
                .executeUpdate();
        return updatedRows > 0;
    }
    
    /**
     * Count movies by genre
     */
    public long countByGenre(String genre) {
        String jpql = "SELECT COUNT(m) FROM Movie m WHERE LOWER(m.genre) LIKE LOWER(CONCAT('%', :genre, '%'))";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("genre", genre);
        return query.getSingleResult();
    }
    
    /**
     * Count available movies
     */
    public long countAvailableMovies() {
        String jpql = "SELECT COUNT(m) FROM Movie m WHERE m.quantity > 0";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        return query.getSingleResult();
    }
    
    /**
     * Get distinct genres
     */
    public List<String> getDistinctGenres() {
        String jpql = "SELECT DISTINCT m.genre FROM Movie m WHERE m.genre IS NOT NULL ORDER BY m.genre";
        TypedQuery<String> query = entityManager.createQuery(jpql, String.class);
        return query.getResultList();
    }
}