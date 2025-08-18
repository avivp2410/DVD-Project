package com.mycompany.blockkbusterr.bean;

import com.mycompany.blockkbusterr.entity.Rental;
import com.mycompany.blockkbusterr.entity.Review;
import com.mycompany.blockkbusterr.entity.User;
import com.mycompany.blockkbusterr.service.RentalService;
import com.mycompany.blockkbusterr.service.ReviewService;
import com.mycompany.blockkbusterr.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Named
@RequestScoped
public class ProfileBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    private RentalService rentalService;
    
    @EJB
    private ReviewService reviewService;
    
    @EJB
    private UserService userService;
    
    @Inject
    private SessionBean sessionBean;
    
    private User currentUser;
    private List<Rental> userRentals;
    private List<Review> userReviews;
    private boolean editMode = false;
    
    // Edit form fields
    private String editFirstName;
    private String editLastName;
    private String editEmail;
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
    
    @PostConstruct
    public void init() {
        loadUserProfile();
    }
    
    private void loadUserProfile() {
        if (!sessionBean.isAuthenticated()) {
            addMessage(FacesMessage.SEVERITY_WARN, "Please log in to view your profile");
            return;
        }
        
        try {
            currentUser = sessionBean.getCurrentUser();
            if (currentUser != null) {
                // Load user's rental history
                userRentals = rentalService.getRentalsByUser(currentUser.getUserId());
                
                // Load user's reviews
                userReviews = reviewService.getReviewsByUser(currentUser.getUserId());
                
                // Initialize edit form with current values
                resetEditForm();
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error loading profile: " + e.getMessage());
        }
    }
    
    private void resetEditForm() {
        if (currentUser != null) {
            editFirstName = currentUser.getFirstName();
            editLastName = currentUser.getLastName();
            editEmail = currentUser.getEmail();
            currentPassword = "";
            newPassword = "";
            confirmPassword = "";
        }
    }
    
    public void toggleEditMode() {
        editMode = !editMode;
        if (editMode) {
            resetEditForm();
        }
    }
    
    public void cancelEdit() {
        editMode = false;
        resetEditForm();
    }
    
    public void updateProfile() {
        if (!sessionBean.isAuthenticated()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Please log in to update your profile");
            return;
        }
        
        try {
            // Validate required fields
            if (editFirstName == null || editFirstName.trim().isEmpty()) {
                addMessage(FacesMessage.SEVERITY_ERROR, "First name is required");
                return;
            }
            
            if (editLastName == null || editLastName.trim().isEmpty()) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Last name is required");
                return;
            }
            
            if (editEmail == null || editEmail.trim().isEmpty()) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Email is required");
                return;
            }
            
            // Check if password change is requested
            boolean changePassword = newPassword != null && !newPassword.trim().isEmpty();
            
            if (changePassword) {
                // Validate current password
                if (currentPassword == null || currentPassword.trim().isEmpty()) {
                    addMessage(FacesMessage.SEVERITY_ERROR, "Current password is required to change password");
                    return;
                }
                
                // Validate new password
                if (newPassword.length() < 6) {
                    addMessage(FacesMessage.SEVERITY_ERROR, "New password must be at least 6 characters long");
                    return;
                }
                
                // Validate password confirmation
                if (!newPassword.equals(confirmPassword)) {
                    addMessage(FacesMessage.SEVERITY_ERROR, "New password and confirmation do not match");
                    return;
                }
                
                // Verify current password using changePassword method which verifies current password
                if (!userService.changePassword(currentUser.getUserId(), currentPassword, newPassword)) {
                    addMessage(FacesMessage.SEVERITY_ERROR, "Current password is incorrect or password change failed");
                    return;
                }
                
                // Password changed successfully, now update profile
                User updatedUser = userService.updateUserProfile(
                    currentUser.getUserId(),
                    editFirstName.trim(),
                    editLastName.trim(),
                    editEmail.trim()
                );
            } else {
                // Just update profile without password change
                User updatedUser = userService.updateUserProfile(
                    currentUser.getUserId(),
                    editFirstName.trim(),
                    editLastName.trim(),
                    editEmail.trim()
                );
            }
            
            // Reload user data from database to get updated information
            Optional<User> userOpt = userService.findUserById(currentUser.getUserId());
            if (userOpt.isPresent()) {
                User refreshedUser = userOpt.get();
                // Update session with new user data
                sessionBean.setCurrentUser(refreshedUser);
                currentUser = refreshedUser;
                
                editMode = false;
                if (changePassword) {
                    addMessage(FacesMessage.SEVERITY_INFO, "Profile and password updated successfully!");
                } else {
                    addMessage(FacesMessage.SEVERITY_INFO, "Profile updated successfully!");
                }
            } else {
                addMessage(FacesMessage.SEVERITY_ERROR, "Failed to reload user data");
            }
            
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error updating profile: " + e.getMessage());
        }
    }
    
    private void addMessage(FacesMessage.Severity severity, String message) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, message, null));
    }
    
    // Getters and Setters
    public User getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
    
    public List<Rental> getUserRentals() {
        return userRentals;
    }
    
    public void setUserRentals(List<Rental> userRentals) {
        this.userRentals = userRentals;
    }
    
    public List<Review> getUserReviews() {
        return userReviews;
    }
    
    public void setUserReviews(List<Review> userReviews) {
        this.userReviews = userReviews;
    }
    
    public boolean isEditMode() {
        return editMode;
    }
    
    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }
    
    public String getEditFirstName() {
        return editFirstName;
    }
    
    public void setEditFirstName(String editFirstName) {
        this.editFirstName = editFirstName;
    }
    
    public String getEditLastName() {
        return editLastName;
    }
    
    public void setEditLastName(String editLastName) {
        this.editLastName = editLastName;
    }
    
    public String getEditEmail() {
        return editEmail;
    }
    
    public void setEditEmail(String editEmail) {
        this.editEmail = editEmail;
    }
    
    public String getCurrentPassword() {
        return currentPassword;
    }
    
    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }
    
    public String getNewPassword() {
        return newPassword;
    }
    
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    
    public String getConfirmPassword() {
        return confirmPassword;
    }
    
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    
    // Utility methods for the UI
    public boolean hasRentals() {
        return userRentals != null && !userRentals.isEmpty();
    }
    
    public boolean hasReviews() {
        return userReviews != null && !userReviews.isEmpty();
    }
    
    public int getTotalRentals() {
        return userRentals != null ? userRentals.size() : 0;
    }
    
    public int getTotalReviews() {
        return userReviews != null ? userReviews.size() : 0;
    }
    
    public long getActiveRentals() {
        if (userRentals == null) return 0;
        return userRentals.stream()
                .filter(rental -> !rental.isReturned())
                .count();
    }
    
    public String getMemberSince() {
        if (currentUser != null && currentUser.getCreatedAt() != null) {
            return currentUser.getCreatedAt().toLocalDate().toString();
        }
        return "Unknown";
    }
    
    public boolean isProfileLoaded() {
        return currentUser != null;
    }
}