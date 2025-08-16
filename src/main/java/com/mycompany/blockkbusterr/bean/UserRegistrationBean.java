package com.mycompany.blockkbusterr.bean;

import com.mycompany.blockkbusterr.entity.User;
import com.mycompany.blockkbusterr.service.UserService;
import com.mycompany.blockkbusterr.util.PasswordUtil;
import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Request-scoped bean for handling user registration
 */
@Named("registrationBean")
@ViewScoped
public class UserRegistrationBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(UserRegistrationBean.class.getName());
    
    @Inject
    private UserService userService;
    
    @Inject
    private SessionBean sessionBean;
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    private String email;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    private String username;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    @NotBlank(message = "Please confirm your password")
    private String confirmPassword;
    
    private boolean termsAccepted = false;
    private boolean registrationInProgress = false;
    
    /**
     * Handle user registration
     */
    public String register() {
        logger.info("=== REGISTRATION DEBUG: register() method called ===");
        logger.info("Form valid check: " + isFormValid());
        logger.info("Registration in progress: " + registrationInProgress);
        
        if (registrationInProgress) {
            logger.warning("Registration already in progress - preventing double submission");
            return null; // Prevent double submission
        }
        
        registrationInProgress = true;
        
        try {
            // Validate form
            if (!validateForm()) {
                return null;
            }
            
            // Register the user
            User newUser = userService.registerUser(firstName, lastName, email, username, password);
            
            if (newUser != null) {
                logger.info("New user registered: " + newUser.getUsername());
                
                // Auto-login the new user
                sessionBean.setCurrentUser(newUser);
                
                addSuccessMessage("Registration successful! Welcome to Blockk Busterr, " + newUser.getFirstName() + "!");
                
                // Clear the form
                clearForm();
                
                // Redirect to main page
                return "mainPage.xhtml?faces-redirect=true";
                
            } else {
                addErrorMessage("Registration failed. Please try again.");
                return null;
            }
            
        } catch (IllegalArgumentException e) {
            logger.warning("Registration validation error: " + e.getMessage());
            addErrorMessage(e.getMessage());
            return null;
            
        } catch (Exception e) {
            logger.severe("Registration error: " + e.getMessage());
            addErrorMessage("An unexpected error occurred during registration. Please try again.");
            return null;
            
        } finally {
            registrationInProgress = false;
        }
    }
    
    /**
     * Navigate back to login page
     */
    public String goToLogin() {
        return "login.xhtml?faces-redirect=true";
    }
    
    /**
     * Check username availability via AJAX
     */
    public void checkUsernameAvailability(AjaxBehaviorEvent event) {
        if (username != null && !username.trim().isEmpty() && username.length() >= 3) {
            try {
                boolean available = userService.isUsernameAvailable(username.trim());
                if (!available) {
                    addFieldError("username", "Username is already taken");
                }
            } catch (Exception e) {
                logger.warning("Error checking username availability: " + e.getMessage());
            }
        }
    }
    
    /**
     * Check email availability via AJAX
     */
    public void checkEmailAvailability(AjaxBehaviorEvent event) {
        if (email != null && !email.trim().isEmpty() &&
            email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
            try {
                boolean available = userService.isEmailAvailable(email.trim());
                if (!available) {
                    addFieldError("email", "Email is already registered");
                }
            } catch (Exception e) {
                logger.warning("Error checking email availability: " + e.getMessage());
            }
        }
    }
    
    /**
     * Validate password confirmation via AJAX
     */
    public void validatePasswordConfirmation(AjaxBehaviorEvent event) {
        if (password != null && confirmPassword != null && 
            !password.equals(confirmPassword)) {
            addFieldError("confirmPassword", "Passwords do not match");
        }
    }
    
    /**
     * Validate the entire form
     */
    private boolean validateForm() {
        boolean valid = true;
        
        // Check required fields
        if (firstName == null || firstName.trim().isEmpty()) {
            addErrorMessage("First name is required");
            valid = false;
        }
        
        if (lastName == null || lastName.trim().isEmpty()) {
            addErrorMessage("Last name is required");
            valid = false;
        }
        
        if (email == null || email.trim().isEmpty()) {
            addErrorMessage("Email is required");
            valid = false;
        }
        
        if (username == null || username.trim().isEmpty()) {
            addErrorMessage("Username is required");
            valid = false;
        }
        
        if (password == null || password.trim().isEmpty()) {
            addErrorMessage("Password is required");
            valid = false;
        }
        
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            addErrorMessage("Please confirm your password");
            valid = false;
        }
        
        // Check password match
        if (password != null && confirmPassword != null && !password.equals(confirmPassword)) {
            addErrorMessage("Passwords do not match");
            valid = false;
        }
        
        // Check terms acceptance
        if (!termsAccepted) {
            addErrorMessage("You must accept the terms and conditions");
            valid = false;
        }
        
        // Check field lengths
        if (firstName != null && (firstName.length() < 2 || firstName.length() > 50)) {
            addErrorMessage("First name must be between 2 and 50 characters");
            valid = false;
        }
        
        if (lastName != null && (lastName.length() < 2 || lastName.length() > 50)) {
            addErrorMessage("Last name must be between 2 and 50 characters");
            valid = false;
        }
        
        if (username != null && (username.length() < 3 || username.length() > 30)) {
            addErrorMessage("Username must be between 3 and 30 characters");
            valid = false;
        }
        
        if (password != null && !PasswordUtil.isValidPassword(password)) {
            addErrorMessage("Password must be at least 6 characters and contain both letters and numbers");
            valid = false;
        }
        
        return valid;
    }
    
    /**
     * Check if the registration form is valid
     */
    public boolean isFormValid() {
        boolean firstNameValid = firstName != null && !firstName.trim().isEmpty();
        boolean lastNameValid = lastName != null && !lastName.trim().isEmpty();
        boolean emailValid = email != null && !email.trim().isEmpty();
        boolean usernameValid = username != null && !username.trim().isEmpty();
        boolean passwordValid = password != null && !password.trim().isEmpty();
        boolean passwordUtilValid = passwordValid && PasswordUtil.isValidPassword(password);
        boolean confirmPasswordValid = confirmPassword != null && !confirmPassword.trim().isEmpty();
        boolean passwordsMatch = passwordValid && confirmPasswordValid && password.equals(confirmPassword);
        
        logger.info("=== FORM VALIDATION DEBUG ===");
        logger.info("First name valid: " + firstNameValid + " (value: '" + firstName + "')");
        logger.info("Last name valid: " + lastNameValid + " (value: '" + lastName + "')");
        logger.info("Email valid: " + emailValid + " (value: '" + email + "')");
        logger.info("Username valid: " + usernameValid + " (value: '" + username + "')");
        logger.info("Password valid: " + passwordValid + " (value: '" + (password != null ? "[" + password.length() + " chars]" : "null") + "')");
        logger.info("Password util valid: " + passwordUtilValid);
        logger.info("Confirm password valid: " + confirmPasswordValid + " (value: '" + (confirmPassword != null ? "[" + confirmPassword.length() + " chars]" : "null") + "')");
        logger.info("Passwords match: " + passwordsMatch);
        logger.info("Terms accepted: " + termsAccepted);
        
        boolean isValid = firstNameValid && lastNameValid && emailValid && usernameValid &&
                         passwordValid && passwordUtilValid && confirmPasswordValid &&
                         passwordsMatch && termsAccepted;
        
        logger.info("Overall form valid: " + isValid);
        return isValid;
    }
    
    /**
     * Get CSS class for register button
     */
    public String getRegisterButtonClass() {
        return isFormValid() ? "register-button enabled" : "register-button disabled";
    }
    
    /**
     * Check if register button should be disabled
     */
    public boolean isRegisterButtonDisabled() {
        boolean formValid = isFormValid();
        boolean disabled = !formValid || registrationInProgress;
        logger.info("=== BUTTON STATE DEBUG ===");
        logger.info("Form valid: " + formValid);
        logger.info("Registration in progress: " + registrationInProgress);
        logger.info("Button disabled: " + disabled);
        return disabled;
    }
    
    /**
     * Clear the registration form
     */
    private void clearForm() {
        firstName = "";
        lastName = "";
        email = "";
        username = "";
        password = "";
        confirmPassword = "";
        termsAccepted = false;
    }
    
    /**
     * Add error message to faces context
     */
    private void addErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }
    
    /**
     * Add success message to faces context
     */
    private void addSuccessMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
    }
    
    /**
     * Add field-specific error message
     */
    private void addFieldError(String fieldId, String message) {
        FacesContext.getCurrentInstance().addMessage("registrationForm:" + fieldId,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }
    
    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
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
    
    public String getConfirmPassword() {
        return confirmPassword;
    }
    
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    
    public boolean isTermsAccepted() {
        return termsAccepted;
    }
    
    public void setTermsAccepted(boolean termsAccepted) {
        this.termsAccepted = termsAccepted;
    }
    
    public boolean isRegistrationInProgress() {
        return registrationInProgress;
    }
}