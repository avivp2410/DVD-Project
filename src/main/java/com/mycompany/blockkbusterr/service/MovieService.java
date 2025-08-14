package com.mycompany.blockkbusterr.service;

import com.mycompany.blockkbusterr.entity.Movie;
import com.mycompany.blockkbusterr.repository.MovieRepository;
import com.mycompany.blockkbusterr.repository.ReviewRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class MovieService {
    
    @Inject
    private MovieRepository movieRepository;
    
    @Inject
    private ReviewRepository reviewRepository;
    
    /**
     * Add a new movie
     */
    public Movie addMovie(String title, LocalDate releaseDate, Integer duration, String genre, Integer quantity, String description, String imageUrl) {
        // Validate input
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (releaseDate == null) {
            throw new IllegalArgumentException("Release date is required");
        }
        if (duration == null || duration <= 0) {
            throw new IllegalArgumentException("Duration must be a positive number");
        }
        if (genre == null || genre.trim().isEmpty()) {
            throw new IllegalArgumentException("Genre is required");
        }
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        
        // Create new movie
        Movie movie = new Movie();
        movie.setTitle(title.trim());
        movie.setReleaseDate(releaseDate);
        movie.setDuration(duration);
        movie.setGenre(genre.trim());
        movie.setQuantity(quantity);
        movie.setDescription(description != null ? description.trim() : null);
        movie.setImageUrl(imageUrl != null ? imageUrl.trim() : null);
        movie.setActive(true);
        
        return movieRepository.save(movie);
    }
    
    /**
     * Update an existing movie
     */
    public Movie updateMovie(Long movieId, String title, LocalDate releaseDate, Integer duration, String genre, Integer quantity, String description, String imageUrl) {
        Optional<Movie> movieOpt = movieRepository.findById(movieId);
        if (movieOpt.isEmpty()) {
            throw new IllegalArgumentException("Movie not found");
        }
        
        Movie movie = movieOpt.get();
        
        // Validate input
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (releaseDate == null) {
            throw new IllegalArgumentException("Release date is required");
        }
        if (duration == null || duration <= 0) {
            throw new IllegalArgumentException("Duration must be a positive number");
        }
        if (genre == null || genre.trim().isEmpty()) {
            throw new IllegalArgumentException("Genre is required");
        }
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        
        // Update movie fields
        movie.setTitle(title.trim());
        movie.setReleaseDate(releaseDate);
        movie.setDuration(duration);
        movie.setGenre(genre.trim());
        movie.setQuantity(quantity);
        movie.setDescription(description != null ? description.trim() : null);
        movie.setImageUrl(imageUrl != null ? imageUrl.trim() : null);
        
        return movieRepository.update(movie);
    }
    
    /**
     * Find movie by ID
     */
    public Optional<Movie> findMovieById(Long movieId) {
        return movieRepository.findById(movieId);
    }
    
    /**
     * Get all movies
     */
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }
    
    /**
     * Get available movies (quantity > 0)
     */
    public List<Movie> getAvailableMovies() {
        return movieRepository.findAvailableMovies();
    }
    
    /**
     * Search movies by title
     */
    public List<Movie> searchMoviesByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return getAllMovies();
        }
        return movieRepository.findByTitle(title.trim());
    }
    
    /**
     * Search movies by genre
     */
    public List<Movie> searchMoviesByGenre(String genre) {
        if (genre == null || genre.trim().isEmpty()) {
            return getAllMovies();
        }
        return movieRepository.findByGenre(genre.trim());
    }
    
    /**
     * Search movies by multiple criteria
     */
    public List<Movie> searchMovies(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllMovies();
        }
        return movieRepository.searchMovies(searchTerm.trim());
    }
    
    /**
     * Get movies by release year
     */
    public List<Movie> getMoviesByReleaseYear(int year) {
        return movieRepository.findByReleaseYear(year);
    }
    
    /**
     * Get movies by release year range
     */
    public List<Movie> getMoviesByReleaseYearRange(int startYear, int endYear) {
        return movieRepository.findByReleaseYearRange(startYear, endYear);
    }
    
    /**
     * Get movies by duration range
     */
    public List<Movie> getMoviesByDurationRange(int minDuration, int maxDuration) {
        return movieRepository.findByDurationRange(minDuration, maxDuration);
    }
    
    /**
     * Get newest movies
     */
    public List<Movie> getNewestMovies(int limit) {
        return movieRepository.findNewestMovies(limit);
    }
    
    /**
     * Get most popular movies
     */
    public List<Movie> getMostPopularMovies(int limit) {
        return movieRepository.findMostPopularMovies(limit);
    }
    
    /**
     * Get movies with minimum rating
     */
    public List<Movie> getMoviesWithMinimumRating(double minRating) {
        return movieRepository.findByMinimumRating(minRating);
    }
    
    /**
     * Get low stock movies
     */
    public List<Movie> getLowStockMovies(int threshold) {
        return movieRepository.findLowStockMovies(threshold);
    }
    
    /**
     * Get out of stock movies
     */
    public List<Movie> getOutOfStockMovies() {
        return movieRepository.findOutOfStockMovies();
    }
    
    /**
     * Update movie quantity
     */
    public boolean updateMovieQuantity(Long movieId, int newQuantity) {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        return movieRepository.updateQuantity(movieId, newQuantity);
    }
    
    /**
     * Increase movie quantity (for returns)
     */
    public boolean increaseMovieQuantity(Long movieId) {
        return movieRepository.increaseQuantity(movieId);
    }
    
    /**
     * Decrease movie quantity (for rentals)
     */
    public boolean decreaseMovieQuantity(Long movieId) {
        return movieRepository.decreaseQuantity(movieId);
    }
    
    /**
     * Check if movie is available for rental
     */
    public boolean isMovieAvailable(Long movieId) {
        Optional<Movie> movieOpt = movieRepository.findById(movieId);
        return movieOpt.isPresent() && movieOpt.get().isAvailable();
    }
    
    /**
     * Get movie availability count
     */
    public int getMovieAvailabilityCount(Long movieId) {
        Optional<Movie> movieOpt = movieRepository.findById(movieId);
        return movieOpt.map(Movie::getQuantity).orElse(0);
    }
    
    /**
     * Delete movie
     */
    public boolean deleteMovie(Long movieId) {
        return movieRepository.deleteById(movieId);
    }
    
    /**
     * Get all distinct genres
     */
    public List<String> getAllGenres() {
        return movieRepository.getDistinctGenres();
    }
    
    /**
     * Get movie statistics
     */
    public MovieStats getMovieStats() {
        long totalMovies = movieRepository.count();
        long availableMovies = movieRepository.countAvailableMovies();
        List<Movie> lowStockMovies = movieRepository.findLowStockMovies(5);
        List<Movie> outOfStockMovies = movieRepository.findOutOfStockMovies();
        
        return new MovieStats(totalMovies, availableMovies, lowStockMovies.size(), outOfStockMovies.size());
    }
    
    /**
     * Get movie with average rating
     */
    public MovieWithRating getMovieWithRating(Long movieId) {
        Optional<Movie> movieOpt = movieRepository.findById(movieId);
        if (movieOpt.isEmpty()) {
            return null;
        }
        
        Movie movie = movieOpt.get();
        Double averageRating = reviewRepository.getAverageRatingForMovie(movieId);
        long reviewCount = reviewRepository.countReviewsForMovie(movieId);
        
        return new MovieWithRating(movie, averageRating != null ? averageRating : 0.0, reviewCount);
    }
    
    /**
     * Get all movies with their ratings
     */
    public List<MovieWithRating> getAllMoviesWithRatings() {
        List<Movie> movies = movieRepository.findAll();
        return movies.stream()
                .map(movie -> {
                    Double avgRating = reviewRepository.getAverageRatingForMovie(movie.getMovieId());
                    long reviewCount = reviewRepository.countReviewsForMovie(movie.getMovieId());
                    return new MovieWithRating(movie, avgRating != null ? avgRating : 0.0, reviewCount);
                })
                .toList();
    }
    
    // Inner class for movie statistics
    public static class MovieStats {
        private final long totalMovies;
        private final long availableMovies;
        private final long lowStockMovies;
        private final long outOfStockMovies;
        
        public MovieStats(long totalMovies, long availableMovies, long lowStockMovies, long outOfStockMovies) {
            this.totalMovies = totalMovies;
            this.availableMovies = availableMovies;
            this.lowStockMovies = lowStockMovies;
            this.outOfStockMovies = outOfStockMovies;
        }
        
        public long getTotalMovies() { return totalMovies; }
        public long getAvailableMovies() { return availableMovies; }
        public long getLowStockMovies() { return lowStockMovies; }
        public long getOutOfStockMovies() { return outOfStockMovies; }
    }
    
    // Inner class for movie with rating
    public static class MovieWithRating {
        private final Movie movie;
        private final double averageRating;
        private final long reviewCount;
        
        public MovieWithRating(Movie movie, double averageRating, long reviewCount) {
            this.movie = movie;
            this.averageRating = averageRating;
            this.reviewCount = reviewCount;
        }
        
        public Movie getMovie() { return movie; }
        public double getAverageRating() { return averageRating; }
        public long getReviewCount() { return reviewCount; }
        
        public String getFormattedRating() {
            return String.format("%.1f", averageRating);
        }
    }
}