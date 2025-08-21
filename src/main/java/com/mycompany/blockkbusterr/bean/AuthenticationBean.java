package com.mycompany.blockkbusterr.bean;

import com.mycompany.blockkbusterr.entity.User;
import com.mycompany.blockkbusterr.service.UserService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Request-scoped bean for handling user authentication (login/logout)
 */
@Named("authBean")
@RequestScoped
public class AuthenticationBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(AuthenticationBean.class.getName());
    
    @Inject
    private UserService userService;
    
    @Inject
    private SessionBean sessionBean;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    private String username;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    private boolean rememberMe = false;
    private boolean loginInProgress = false;
    
    /**
     * Handle user login
     */
    public String login() {
        if (loginInProgress) {
            return null; // Prevent double submission
        }
        
        loginInProgress = true;
        
        try {
            // Validate input
            if (username == null || username.trim().isEmpty()) {
                addErrorMessage("Please enter your username.");
                return null;
            }
            
            if (password == null || password.trim().isEmpty()) {
                addErrorMessage("Please enter your password.");
                return null;
            }
            
            // Attempt authentication
            Optional<User> userOpt = userService.authenticateUser(username.trim(), password);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                // Check if user account is active
                if (!user.getActive()) {
                    addErrorMessage("Your account has been deactivated. Please contact administrator.");
                    logger.warning("Login attempt with deactivated account: " + username);
                    return null;
                }
                
                // Set the authenticated user in session
                sessionBean.setCurrentUser(user);
                
                // Clear the form
                clearForm();
                
                logger.info("Successful login for user: " + user.getUsername());
                
                // Redirect based on user role
                return sessionBean.getHomePageForCurrentUser();
                
            } else {
                addErrorMessage("Invalid username or password. Please try again.");
                logger.warning("Failed login attempt for username: " + username);
                password = ""; // Clear password for security
                return null;
            }
            
        } catch (Exception e) {
            logger.severe("Login error: " + e.getMessage());
            addErrorMessage("An error occurred during login. Please try again.");
            return null;
            
        } finally {
            loginInProgress = false;
        }
    }
    
    /**
     * Handle user logout
     */
    public String logout() {
        return sessionBean.logout();
    }
    
    /**
     * Navigate to registration page
     */
    public String goToRegistration() {
        return "register.xhtml?faces-redirect=true";
    }
    
    /**
     * Navigate to main page (for guest users)
     */
    public String goToMainPage() {
        return "mainPage.xhtml?faces-redirect=true";
    }
    
    /**
     * Check if user is currently authenticated
     */
    public boolean isAuthenticated() {
        return sessionBean.isAuthenticated();
    }
    
    /**
     * Get display name for current user
     */
    public String getCurrentUserDisplayName() {
        return sessionBean.getCurrentUserDisplayName();
    }
    
    /**
     * Validate login form
     */
    public boolean isFormValid() {
        return username != null && !username.trim().isEmpty() &&
               password != null && !password.trim().isEmpty();
    }
    
    /**
     * Pre-render check for login page
     */
    public void checkAlreadyLoggedIn() {
        if (sessionBean.isAuthenticated()) {
            try {
                String redirectPage = sessionBean.getHomePageForCurrentUser();
                FacesContext.getCurrentInstance().getExternalContext().redirect(redirectPage);
            } catch (Exception e) {
                logger.warning("Error redirecting already logged in user: " + e.getMessage());
            }
        }
    }
    
    /**
     * Clear the login form
     */
    private void clearForm() {
        username = "";
        password = "";
        rememberMe = false;
    }
    
    /**
     * Add error message to faces context
     */
    private void addErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }
    
    /**
     * Add info message to faces context
     */
    private void addInfoMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
    }
    
    /**
     * Add warning message to faces context
     */
    private void addWarningMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_WARN, message, null));
    }
    
    /**
     * Get CSS class for login button based on form validity
     */
    public String getLoginButtonClass() {
        // Always show button as enabled unless login is in progress
        // Form validation will be handled when user clicks submit
        return loginInProgress ? "login-button disabled" : "login-button enabled";
    }
    
    /**
     * Check if login button should be disabled
     * Only disable during login processing, not for empty fields
     */
    public boolean isLoginButtonDisabled() {
        // Only disable when login is in progress
        // Form validation will be handled in the login() method itself
        return loginInProgress;
    }
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public boolean isRememberMe() {
        return rememberMe;
    }
    
    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
    
    public boolean isLoginInProgress() {
        return loginInProgress;
    }
}