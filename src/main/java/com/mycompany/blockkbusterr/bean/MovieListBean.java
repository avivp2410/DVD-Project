package com.mycompany.blockkbusterr.bean;

import com.mycompany.blockkbusterr.entity.Movie;
import com.mycompany.blockkbusterr.service.MovieService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * JSF Managed Bean for movie listing and search functionality on the main page
 */
@Named("movieListBean")
@ViewScoped
public class MovieListBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(MovieListBean.class.getName());
    
    @Inject
    private MovieService movieService;
    
    private List<Movie> movies;
    private List<Movie> filteredMovies;
    private String searchTerm = "";
    private String selectedGenre = "";
    private List<String> availableGenres;
    private boolean showAvailableOnly = false;
    
    @PostConstruct
    public void init() {
        loadMovies();
        loadGenres();
    }
    
    /**
     * Load all movies from the backend
     */
    private void loadMovies() {
        try {
            movies = movieService.getAllMovies();
            filteredMovies = new ArrayList<>(movies);
            logger.info("Loaded " + movies.size() + " movies");
        } catch (Exception e) {
            logger.severe("Error loading movies: " + e.getMessage());
            movies = new ArrayList<>();
            filteredMovies = new ArrayList<>();
        }
    }
    
    /**
     * Load available genres from the backend
     */
    private void loadGenres() {
        try {
            availableGenres = movieService.getAllGenres();
            logger.info("Loaded " + availableGenres.size() + " genres");
        } catch (Exception e) {
            logger.severe("Error loading genres: " + e.getMessage());
            availableGenres = new ArrayList<>();
        }
    }
    
    /**
     * Search movies based on search term, genre filter, and availability
     */
    public void searchMovies() {
        try {
            logger.info("Starting search with searchTerm='" + searchTerm + "', selectedGenre='" + selectedGenre + "', showAvailableOnly=" + showAvailableOnly);
            filteredMovies = new ArrayList<>();
            
            // Start with all movies or available movies only
            List<Movie> sourceMovies = showAvailableOnly ?
                movieService.getAvailableMovies() : movieService.getAllMovies();
            
            logger.info("Source movies count: " + sourceMovies.size());
            
            for (Movie movie : sourceMovies) {
                boolean matches = true;
                
                // Filter by search term (title or description)
                if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                    String search = searchTerm.toLowerCase().trim();
                    boolean titleMatch = movie.getTitle().toLowerCase().contains(search);
                    boolean descMatch = movie.getDescription() != null &&
                                      movie.getDescription().toLowerCase().contains(search);
                    matches = titleMatch || descMatch;
                    logger.info("Movie '" + movie.getTitle() + "' search filter: " + matches);
                }
                
                // Filter by genre
                if (matches && selectedGenre != null && !selectedGenre.trim().isEmpty()) {
                    boolean genreMatch = movie.getGenre().equalsIgnoreCase(selectedGenre.trim());
                    matches = genreMatch;
                    logger.info("Movie '" + movie.getTitle() + "' (genre: '" + movie.getGenre() + "') vs selected '" + selectedGenre + "': " + matches);
                }
                
                if (matches) {
                    filteredMovies.add(movie);
                }
            }
            
            logger.info("Search completed. Found " + filteredMovies.size() + " movies");
            
        } catch (Exception e) {
            logger.severe("Error searching movies: " + e.getMessage());
            filteredMovies = new ArrayList<>();
        }
    }
    
    /**
     * Clear all filters and show all movies
     */
    public void clearFilters() {
        searchTerm = "";
        selectedGenre = "";
        showAvailableOnly = false;
        loadMovies();
    }
    
    /**
     * Filter by genre
     */
    public void filterByGenre() {
        searchMovies();
    }
    
    /**
     * Toggle availability filter
     */
    public void toggleAvailabilityFilter() {
        logger.info("toggleAvailabilityFilter() called - showAvailableOnly: " + showAvailableOnly);
        searchMovies();
    }
    
    /**
     * AJAX listener method for availability filter toggle
     */
    public void toggleAvailabilityFilter(AjaxBehaviorEvent event) {
        logger.info("toggleAvailabilityFilter(AjaxBehaviorEvent) called - showAvailableOnly: " + showAvailableOnly);
        searchMovies();
    }
    
    /**
     * AJAX listener method for search input
     */
    public void onSearchKeyup(AjaxBehaviorEvent event) {
        searchMovies();
    }
    
    /**
     * AJAX listener method for genre filter
     */
    public void onGenreChange(AjaxBehaviorEvent event) {
        logger.info("Genre changed to: " + selectedGenre);
        searchMovies();
    }
    
    /**
     * Get newest movies for highlights
     */
    public List<Movie> getNewestMovies() {
        try {
            return movieService.getNewestMovies(6);
        } catch (Exception e) {
            logger.severe("Error loading newest movies: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Get most popular movies for highlights
     */
    public List<Movie> getMostPopularMovies() {
        try {
            return movieService.getMostPopularMovies(6);
        } catch (Exception e) {
            logger.severe("Error loading popular movies: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Navigate to movie details page
     */
    public String viewMovieDetails(Long movieId) {
        return "movie.xhtml?faces-redirect=true&movieId=" + movieId;
    }
    
    /**
     * Check if a movie is available for rental
     */
    public boolean isMovieAvailable(Movie movie) {
        return movie != null && movie.isAvailable();
    }
    
    /**
     * Get formatted availability status
     */
    public String getAvailabilityStatus(Movie movie) {
        if (movie == null) return "Unknown";
        
        if (movie.getQuantity() == 0) {
            return "Out of Stock";
        } else if (movie.getQuantity() <= 2) {
            return "Limited (" + movie.getQuantity() + " left)";
        } else {
            return "Available (" + movie.getQuantity() + " copies)";
        }
    }
    
    /**
     * Get CSS class for availability status
     */
    public String getAvailabilityClass(Movie movie) {
        if (movie == null || movie.getQuantity() == 0) {
            return "out-of-stock";
        } else if (movie.getQuantity() <= 2) {
            return "limited-stock";
        } else {
            return "in-stock";
        }
    }
    
    // Getters and Setters
    public List<Movie> getMovies() {
        return movies;
    }
    
    public List<Movie> getFilteredMovies() {
        logger.info("getFilteredMovies() called, returning " + (filteredMovies != null ? filteredMovies.size() : "null") + " movies");
        if (filteredMovies == null || filteredMovies.isEmpty()) {
            logger.info("filteredMovies is null or empty, calling searchMovies()");
            searchMovies();
        }
        return filteredMovies;
    }
    
    public String getSearchTerm() {
        return searchTerm;
    }
    
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
    
    public String getSelectedGenre() {
        logger.info("getSelectedGenre() called, returning: '" + selectedGenre + "'");
        return selectedGenre;
    }
    
    public void setSelectedGenre(String selectedGenre) {
        logger.info("setSelectedGenre() called with: '" + selectedGenre + "'");
        this.selectedGenre = selectedGenre;
    }
    
    public List<String> getAvailableGenres() {
        return availableGenres;
    }
    
    public boolean isShowAvailableOnly() {
        return showAvailableOnly;
    }
    
    public void setShowAvailableOnly(boolean showAvailableOnly) {
        this.showAvailableOnly = showAvailableOnly;
    }
    
    public int getMovieCount() {
        return filteredMovies != null ? filteredMovies.size() : 0;
    }
    
    public int getTotalMovieCount() {
        return movies != null ? movies.size() : 0;
    }
}