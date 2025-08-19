package com.mycompany.blockkbusterr.bean;

import com.mycompany.blockkbusterr.entity.User;
import com.mycompany.blockkbusterr.entity.UserRole;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Session-scoped bean for managing user session state and authentication
 */
@Named("sessionBean")
@SessionScoped
public class SessionBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(SessionBean.class.getName());
    
    private User currentUser;
    private boolean authenticated = false;
    
    /**
     * Set the current authenticated user
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        this.authenticated = (user != null);
        
        if (authenticated) {
            logger.info("User authenticated: " + user.getUsername());
            addMessage(FacesMessage.SEVERITY_INFO, "Welcome back, " + user.getFirstName() + "!");
        }
    }
    
    /**
     * Clear the current user session (logout)
     */
    public String logout() {
        if (currentUser != null) {
            logger.info("User logged out: " + currentUser.getUsername());
            addMessage(FacesMessage.SEVERITY_INFO, "You have been logged out successfully.");
        }
        
        this.currentUser = null;
        this.authenticated = false;
        
        // Invalidate the session
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        
        return "login.xhtml?faces-redirect=true";
    }
    
    /**
     * Check if user is authenticated
     */
    public boolean isAuthenticated() {
        return authenticated && currentUser != null;
    }
    
    /**
     * Check if current user is an admin
     */
    public boolean isAdmin() {
        return isAuthenticated() && currentUser.getRole() == UserRole.ADMIN;
    }
    
    /**
     * Check if current user is a regular user
     */
    public boolean isUser() {
        return isAuthenticated() && currentUser.getRole() == UserRole.USER;
    }
    
    /**
     * Get the current user's display name
     */
    public String getCurrentUserDisplayName() {
        if (currentUser != null) {
            return currentUser.getFirstName() + " " + currentUser.getLastName();
        }
        return "Guest";
    }
    
    /**
     * Get the current user's username
     */
    public String getCurrentUsername() {
        if (currentUser != null) {
            return currentUser.getUsername();
        }
        return "guest";
    }
    
    /**
     * Check if the current user can access admin features
     */
    public void requireAdmin() {
        if (!isAdmin()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Access denied. Admin privileges required.");
            throw new SecurityException("Admin access required");
        }
    }
    
    /**
     * Check if the current user is authenticated
     */
    public void requireAuthentication() {
        if (!isAuthenticated()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Please log in to access this feature.");
            throw new SecurityException("Authentication required");
        }
    }
    
    /**
     * Check if the current user can access a specific user's data
     */
    public boolean canAccessUserData(Long userId) {
        if (!isAuthenticated()) {
            return false;
        }
        
        // Admin can access any user's data
        if (isAdmin()) {
            return true;
        }
        
        // User can only access their own data
        return currentUser.getUserId().equals(userId);
    }
    
    /**
     * Navigate to appropriate page after login based on user role
     */
    public String getHomePageForCurrentUser() {
        if (!isAuthenticated()) {
            return "login.xhtml?faces-redirect=true";
        }
        
        if (isAdmin()) {
            return "adminPage.xhtml?faces-redirect=true";
        } else {
            return "mainPage.xhtml?faces-redirect=true";
        }
    }
    
    /**
     * Check access and redirect if necessary
     */
    public String checkAccess(String requiredRole) {
        if (!isAuthenticated()) {
            addMessage(FacesMessage.SEVERITY_WARN, "Please log in to continue.");
            return "login.xhtml?faces-redirect=true";
        }
        
        if ("admin".equals(requiredRole) && !isAdmin()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Access denied. Admin privileges required.");
            return getHomePageForCurrentUser();
        }
        
        return null; // Access granted
    }
    
    /**
     * Get user initials for display
     */
    public String getCurrentUserInitials() {
        if (currentUser != null) {
            String first = currentUser.getFirstName();
            String last = currentUser.getLastName();
            
            StringBuilder initials = new StringBuilder();
            if (first != null && !first.isEmpty()) {
                initials.append(first.charAt(0));
            }
            if (last != null && !last.isEmpty()) {
                initials.append(last.charAt(0));
            }
            
            return initials.toString().toUpperCase();
        }
        return "G";
    }
    
    /**
     * Add a faces message
     */
    private void addMessage(FacesMessage.Severity severity, String message) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            context.addMessage(null, new FacesMessage(severity, message, null));
        }
    }
    
    /**
     * Get session timeout warning
     */
    public boolean isSessionNearExpiry() {
        // Implementation for session timeout warning
        // This could be enhanced with JavaScript to check session time
        return false;
    }
    
    // Getters and Setters
    public User getCurrentUser() {
        return currentUser;
    }
    
    public Long getCurrentUserId() {
        return currentUser != null ? currentUser.getUserId() : null;
    }
    
    public String getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole().name() : "GUEST";
    }
    
    public String getCurrentUserEmail() {
        return currentUser != null ? currentUser.getEmail() : "";
    }
    
    public boolean isGuest() {
        return !isAuthenticated();
    }
    
    /**
     * Navigate to profile page
     */
    public String goToProfile() {
        return "profile.xhtml?faces-redirect=true";
    }
    
    /**
     * Navigate to login page
     */
    public String goToLogin() {
        return "login.xhtml?faces-redirect=true";
    }
    
    /**
     * Navigate to register page
     */
    public String goToRegister() {
        return "register.xhtml?faces-redirect=true";
    }
    
    /**
     * Navigate to main page
     */
    public String goToMainPage() {
        return "mainPage.xhtml?faces-redirect=true";
    }
}