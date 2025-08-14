package com.mycompany.blockkbusterr.dto;

import com.mycompany.blockkbusterr.entity.User;
import com.mycompany.blockkbusterr.entity.UserRole;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;

@XmlRootElement(name = "userResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserResponse {
    
    @XmlElement
    private Long userId;
    
    @XmlElement
    private String firstName;
    
    @XmlElement
    private String lastName;
    
    @XmlElement
    private String email;
    
    @XmlElement
    private String username;
    
    @XmlElement
    private String role;
    
    @XmlElement
    private Boolean active;
    
    @XmlElement
    private String createdAt;
    
    @XmlElement
    private boolean success;
    
    @XmlElement
    private String message;
    
    // Default constructor for JAXB
    public UserResponse() {}
    
    public UserResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public UserResponse(User user, boolean success, String message) {
        this.success = success;
        this.message = message;
        if (user != null) {
            this.userId = user.getUserId();
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();
            this.email = user.getEmail();
            this.username = user.getUsername();
            this.role = user.getRole() != null ? user.getRole().name() : null;
            this.active = user.getActive();
            this.createdAt = user.getCreatedAt() != null ? user.getCreatedAt().toString() : null;
        }
    }
    
    // Static factory methods
    public static UserResponse success(User user) {
        return new UserResponse(user, true, "Operation successful");
    }
    
    public static UserResponse success(User user, String message) {
        return new UserResponse(user, true, message);
    }
    
    public static UserResponse error(String message) {
        return new UserResponse(false, message);
    }
    
    // Getters and Setters
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
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
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return null;
    }
}