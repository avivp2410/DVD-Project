package com.mycompany.blockkbusterr.bean;

import com.mycompany.blockkbusterr.entity.Movie;
import com.mycompany.blockkbusterr.service.MovieService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * JSF Managed Bean for movie management (add/edit)
 */
@Named("movieManagementBean")
@ViewScoped
public class MovieManagementBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(MovieManagementBean.class.getName());
    
    @Inject
    private MovieService movieService;
    
    @Inject
    private SessionBean sessionBean;
    
    // Movie properties for form binding
    private String title;
    private Integer releaseYear;
    private Integer duration;
    private String genre;
    private Integer quantity;
    private String description;
    
    // Edit mode properties
    private Long movieId;
    private boolean editMode = false;
    
    @PostConstruct
    public void init() {
        try {
            // Check admin access
            if (!sessionBean.isAdmin()) {
                FacesContext.getCurrentInstance().getExternalContext()
                    .redirect("mainPage.xhtml?faces-redirect=true");
                return;
            }
            
            // Check if this is edit mode (movieId parameter provided)
            String movieIdParam = FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .getRequestParameterMap()
                    .get("movieId");
            
            if (movieIdParam != null && !movieIdParam.isEmpty()) {
                try {
                    movieId = Long.parseLong(movieIdParam);
                    editMode = true;
                    loadMovieForEdit();
                } catch (NumberFormatException e) {
                    logger.severe("Invalid movie ID: " + movieIdParam);
                    addErrorMessage("Invalid movie ID provided.");
                }
            }
            
        } catch (Exception e) {
            logger.severe("Error initializing MovieManagementBean: " + e.getMessage());
            addErrorMessage("Error loading movie management page.");
        }
    }
    
    /**
     * Load movie data for editing
     */
    private void loadMovieForEdit() {
        try {
            Optional<Movie> movieOpt = movieService.findMovieById(movieId);
            if (movieOpt.isPresent()) {
                Movie movie = movieOpt.get();
                
                // Pre-fill form fields
                this.title = movie.getTitle();
                this.releaseYear = movie.getReleaseYear();
                this.duration = movie.getDuration();
                this.genre = movie.getGenre();
                this.quantity = movie.getQuantity();
                this.description = movie.getDescription();
                
                logger.info("Loaded movie for editing: " + movie.getTitle());
            } else {
                addErrorMessage("Movie not found with ID: " + movieId);
                editMode = false;
            }
        } catch (Exception e) {
            logger.severe("Error loading movie for edit: " + e.getMessage());
            addErrorMessage("Error loading movie details.");
            editMode = false;
        }
    }
    
    /**
     * Add a new movie
     */
    public String addMovie() {
        try {
            logger.info("Adding new movie: " + title);
            
            Movie movie = movieService.addMovie(title, releaseYear, duration, genre, quantity, description);
            
            addSuccessMessage("Movie '" + movie.getTitle() + "' added successfully!");
            logger.info("Movie added successfully with ID: " + movie.getMovieId());
            
            // Clear form
            clearForm();
            
            // Redirect to admin page
            return "adminPage.xhtml?faces-redirect=true";
            
        } catch (IllegalArgumentException e) {
            logger.warning("Validation error adding movie: " + e.getMessage());
            addErrorMessage(e.getMessage());
            return null;
        } catch (Exception e) {
            logger.severe("Error adding movie: " + e.getMessage());
            addErrorMessage("Error adding movie. Please try again.");
            return null;
        }
    }
    
    /**
     * Update existing movie
     */
    public String updateMovie() {
        try {
            logger.info("Updating movie with ID: " + movieId);
            
            Movie movie = movieService.updateMovie(movieId, title, releaseYear, duration, genre, quantity, description);
            
            addSuccessMessage("Movie '" + movie.getTitle() + "' updated successfully!");
            logger.info("Movie updated successfully: " + movie.getMovieId());
            
            // Redirect to admin page
            return "adminPage.xhtml?faces-redirect=true";
            
        } catch (IllegalArgumentException e) {
            logger.warning("Validation error updating movie: " + e.getMessage());
            addErrorMessage(e.getMessage());
            return null;
        } catch (Exception e) {
            logger.severe("Error updating movie: " + e.getMessage());
            addErrorMessage("Error updating movie. Please try again.");
            return null;
        }
    }
    
    /**
     * Cancel and return to admin page
     */
    public String cancel() {
        return "adminPage.xhtml?faces-redirect=true";
    }
    
    /**
     * Clear form fields
     */
    private void clearForm() {
        title = null;
        releaseYear = null;
        duration = null;
        genre = null;
        quantity = null;
        description = null;
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
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public Integer getReleaseYear() {
        return releaseYear;
    }
    
    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }
    
    public Integer getDuration() {
        return duration;
    }
    
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    
    public String getGenre() {
        return genre;
    }
    
    public void setGenre(String genre) {
        this.genre = genre;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Long getMovieId() {
        return movieId;
    }
    
    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }
    
    public boolean isEditMode() {
        return editMode;
    }
    
    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }
    
    // Utility methods for UI
    public String getPageTitle() {
        return editMode ? "Edit Movie" : "Add New Movie";
    }
    
    public String getSubmitButtonLabel() {
        return editMode ? "Update Movie" : "Add Movie";
    }
}