package com.mycompany.blockkbusterr.webservice;

import com.mycompany.blockkbusterr.dto.MovieRequest;
import com.mycompany.blockkbusterr.dto.MovieResponse;
import com.mycompany.blockkbusterr.entity.Movie;
import com.mycompany.blockkbusterr.service.MovieService;
import com.mycompany.blockkbusterr.service.ReviewService;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.xml.ws.soap.SOAPBinding;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@WebService(
    name = "MovieManagementService",
    serviceName = "MovieManagementService",
    targetNamespace = "http://webservice.blockkbusterr.mycompany.com/"
)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL)
@Stateless
public class MovieManagementWebService {
    
    @Inject
    private MovieService movieService;
    
    @Inject
    private ReviewService reviewService;
    
    /**
     * Add a new movie
     */
    @WebMethod(operationName = "addMovie")
    @WebResult(name = "movieResponse")
    public MovieResponse addMovie(
            @WebParam(name = "movieRequest") MovieRequest request) {
        
        try {
            if (request == null) {
                return MovieResponse.error("Movie request cannot be null");
            }
            
            // Parse release date
            LocalDate releaseDate;
            try {
                releaseDate = LocalDate.parse(request.getReleaseDate());
            } catch (DateTimeParseException e) {
                return MovieResponse.error("Invalid date format. Use YYYY-MM-DD");
            }
            
            Movie movie = movieService.addMovie(
                request.getTitle(),
                releaseDate,
                request.getDuration(),
                request.getGenre(),
                request.getQuantity(),
                request.getDescription(),
                request.getImageUrl()
            );
            
            return MovieResponse.success(movie, "Movie added successfully");
            
        } catch (IllegalArgumentException e) {
            return MovieResponse.error(e.getMessage());
        } catch (Exception e) {
            return MovieResponse.error("Failed to add movie: " + e.getMessage());
        }
    }
    
    /**
     * Update an existing movie
     */
    @WebMethod(operationName = "updateMovie")
    @WebResult(name = "movieResponse")
    public MovieResponse updateMovie(
            @WebParam(name = "movieId") Long movieId,
            @WebParam(name = "movieRequest") MovieRequest request) {
        
        try {
            if (movieId == null) {
                return MovieResponse.error("Movie ID cannot be null");
            }
            
            if (request == null) {
                return MovieResponse.error("Movie request cannot be null");
            }
            
            // Parse release date
            LocalDate releaseDate;
            try {
                releaseDate = LocalDate.parse(request.getReleaseDate());
            } catch (DateTimeParseException e) {
                return MovieResponse.error("Invalid date format. Use YYYY-MM-DD");
            }
            
            Movie movie = movieService.updateMovie(
                movieId,
                request.getTitle(),
                releaseDate,
                request.getDuration(),
                request.getGenre(),
                request.getQuantity(),
                request.getDescription(),
                request.getImageUrl()
            );
            
            return MovieResponse.success(movie, "Movie updated successfully");
            
        } catch (IllegalArgumentException e) {
            return MovieResponse.error(e.getMessage());
        } catch (Exception e) {
            return MovieResponse.error("Failed to update movie: " + e.getMessage());
        }
    }
    
    /**
     * Get movie by ID
     */
    @WebMethod(operationName = "getMovieById")
    @WebResult(name = "movieResponse")
    public MovieResponse getMovieById(
            @WebParam(name = "movieId") Long movieId) {
        
        try {
            if (movieId == null) {
                return MovieResponse.error("Movie ID cannot be null");
            }
            
            Optional<Movie> movieOpt = movieService.findMovieById(movieId);
            if (movieOpt.isPresent()) {
                MovieResponse response = MovieResponse.success(movieOpt.get());
                
                // Add rating information
                double avgRating = reviewService.getAverageRatingForMovie(movieId);
                long reviewCount = reviewService.getReviewCountForMovie(movieId);
                response.setAverageRating(avgRating);
                response.setReviewCount(reviewCount);
                
                return response;
            } else {
                return MovieResponse.error("Movie not found");
            }
            
        } catch (Exception e) {
            return MovieResponse.error("Failed to get movie: " + e.getMessage());
        }
    }
    
    /**
     * Get all movies
     */
    @WebMethod(operationName = "getAllMovies")
    @WebResult(name = "movies")
    public List<MovieResponse> getAllMovies() {
        
        try {
            List<Movie> movies = movieService.getAllMovies();
            return movies.stream()
                    .map(movie -> {
                        MovieResponse response = MovieResponse.success(movie);
                        double avgRating = reviewService.getAverageRatingForMovie(movie.getMovieId());
                        long reviewCount = reviewService.getReviewCountForMovie(movie.getMovieId());
                        response.setAverageRating(avgRating);
                        response.setReviewCount(reviewCount);
                        return response;
                    })
                    .collect(Collectors.toList());
            
        } catch (Exception e) {
            return List.of(MovieResponse.error("Failed to get movies: " + e.getMessage()));
        }
    }
    
    /**
     * Get available movies
     */
    @WebMethod(operationName = "getAvailableMovies")
    @WebResult(name = "movies")
    public List<MovieResponse> getAvailableMovies() {
        
        try {
            List<Movie> movies = movieService.getAvailableMovies();
            return movies.stream()
                    .map(movie -> {
                        MovieResponse response = MovieResponse.success(movie);
                        double avgRating = reviewService.getAverageRatingForMovie(movie.getMovieId());
                        long reviewCount = reviewService.getReviewCountForMovie(movie.getMovieId());
                        response.setAverageRating(avgRating);
                        response.setReviewCount(reviewCount);
                        return response;
                    })
                    .collect(Collectors.toList());
            
        } catch (Exception e) {
            return List.of(MovieResponse.error("Failed to get available movies: " + e.getMessage()));
        }
    }
    
    /**
     * Search movies by title
     */
    @WebMethod(operationName = "searchMoviesByTitle")
    @WebResult(name = "movies")
    public List<MovieResponse> searchMoviesByTitle(
            @WebParam(name = "title") String title) {
        
        try {
            List<Movie> movies = movieService.searchMoviesByTitle(title);
            return movies.stream()
                    .map(movie -> MovieResponse.success(movie))
                    .collect(Collectors.toList());
            
        } catch (Exception e) {
            return List.of(MovieResponse.error("Failed to search movies: " + e.getMessage()));
        }
    }
    
    /**
     * Search movies by genre
     */
    @WebMethod(operationName = "searchMoviesByGenre")
    @WebResult(name = "movies")
    public List<MovieResponse> searchMoviesByGenre(
            @WebParam(name = "genre") String genre) {
        
        try {
            List<Movie> movies = movieService.searchMoviesByGenre(genre);
            return movies.stream()
                    .map(movie -> MovieResponse.success(movie))
                    .collect(Collectors.toList());
            
        } catch (Exception e) {
            return List.of(MovieResponse.error("Failed to search movies by genre: " + e.getMessage()));
        }
    }
    
    /**
     * Search movies by multiple criteria
     */
    @WebMethod(operationName = "searchMovies")
    @WebResult(name = "movies")
    public List<MovieResponse> searchMovies(
            @WebParam(name = "searchTerm") String searchTerm) {
        
        try {
            List<Movie> movies = movieService.searchMovies(searchTerm);
            return movies.stream()
                    .map(movie -> MovieResponse.success(movie))
                    .collect(Collectors.toList());
            
        } catch (Exception e) {
            return List.of(MovieResponse.error("Failed to search movies: " + e.getMessage()));
        }
    }
    
    /**
     * Get movies by release year
     */
    @WebMethod(operationName = "getMoviesByYear")
    @WebResult(name = "movies")
    public List<MovieResponse> getMoviesByYear(
            @WebParam(name = "year") int year) {
        
        try {
            List<Movie> movies = movieService.getMoviesByReleaseYear(year);
            return movies.stream()
                    .map(movie -> MovieResponse.success(movie))
                    .collect(Collectors.toList());
            
        } catch (Exception e) {
            return List.of(MovieResponse.error("Failed to get movies by year: " + e.getMessage()));
        }
    }
    
    /**
     * Get newest movies
     */
    @WebMethod(operationName = "getNewestMovies")
    @WebResult(name = "movies")
    public List<MovieResponse> getNewestMovies(
            @WebParam(name = "limit") int limit) {
        
        try {
            List<Movie> movies = movieService.getNewestMovies(limit);
            return movies.stream()
                    .map(movie -> MovieResponse.success(movie))
                    .collect(Collectors.toList());
            
        } catch (Exception e) {
            return List.of(MovieResponse.error("Failed to get newest movies: " + e.getMessage()));
        }
    }
    
    /**
     * Get most popular movies
     */
    @WebMethod(operationName = "getMostPopularMovies")
    @WebResult(name = "movies")
    public List<MovieResponse> getMostPopularMovies(
            @WebParam(name = "limit") int limit) {
        
        try {
            List<Movie> movies = movieService.getMostPopularMovies(limit);
            return movies.stream()
                    .map(movie -> MovieResponse.success(movie))
                    .collect(Collectors.toList());
            
        } catch (Exception e) {
            return List.of(MovieResponse.error("Failed to get popular movies: " + e.getMessage()));
        }
    }
    
    /**
     * Update movie quantity
     */
    @WebMethod(operationName = "updateMovieQuantity")
    @WebResult(name = "success")
    public boolean updateMovieQuantity(
            @WebParam(name = "movieId") Long movieId,
            @WebParam(name = "quantity") int quantity) {
        
        try {
            if (movieId == null) {
                return false;
            }
            
            return movieService.updateMovieQuantity(movieId, quantity);
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if movie is available
     */
    @WebMethod(operationName = "isMovieAvailable")
    @WebResult(name = "available")
    public boolean isMovieAvailable(
            @WebParam(name = "movieId") Long movieId) {
        
        try {
            if (movieId == null) {
                return false;
            }
            
            return movieService.isMovieAvailable(movieId);
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get movie availability count
     */
    @WebMethod(operationName = "getMovieAvailabilityCount")
    @WebResult(name = "count")
    public int getMovieAvailabilityCount(
            @WebParam(name = "movieId") Long movieId) {
        
        try {
            if (movieId == null) {
                return 0;
            }
            
            return movieService.getMovieAvailabilityCount(movieId);
            
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Delete movie
     */
    @WebMethod(operationName = "deleteMovie")
    @WebResult(name = "success")
    public boolean deleteMovie(
            @WebParam(name = "movieId") Long movieId) {
        
        try {
            if (movieId == null) {
                return false;
            }
            
            return movieService.deleteMovie(movieId);
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get all genres
     */
    @WebMethod(operationName = "getAllGenres")
    @WebResult(name = "genres")
    public List<String> getAllGenres() {
        
        try {
            return movieService.getAllGenres();
            
        } catch (Exception e) {
            return List.of();
        }
    }
    
    /**
     * Get movie statistics
     */
    @WebMethod(operationName = "getMovieStats")
    @WebResult(name = "movieStats")
    public String getMovieStats() {
        
        try {
            MovieService.MovieStats stats = movieService.getMovieStats();
            return String.format("Total: %d, Available: %d, Low Stock: %d, Out of Stock: %d",
                    stats.getTotalMovies(),
                    stats.getAvailableMovies(),
                    stats.getLowStockMovies(),
                    stats.getOutOfStockMovies());
            
        } catch (Exception e) {
            return "Failed to get statistics: " + e.getMessage();
        }
    }
    
    /**
     * Get low stock movies
     */
    @WebMethod(operationName = "getLowStockMovies")
    @WebResult(name = "movies")
    public List<MovieResponse> getLowStockMovies(
            @WebParam(name = "threshold") int threshold) {
        
        try {
            List<Movie> movies = movieService.getLowStockMovies(threshold);
            return movies.stream()
                    .map(movie -> MovieResponse.success(movie))
                    .collect(Collectors.toList());
            
        } catch (Exception e) {
            return List.of(MovieResponse.error("Failed to get low stock movies: " + e.getMessage()));
        }
    }
    
    /**
     * Get out of stock movies
     */
    @WebMethod(operationName = "getOutOfStockMovies")
    @WebResult(name = "movies")
    public List<MovieResponse> getOutOfStockMovies() {
        
        try {
            List<Movie> movies = movieService.getOutOfStockMovies();
            return movies.stream()
                    .map(movie -> MovieResponse.success(movie))
                    .collect(Collectors.toList());
            
        } catch (Exception e) {
            return List.of(MovieResponse.error("Failed to get out of stock movies: " + e.getMessage()));
        }
    }
}