package com.mycompany.blockkbusterr.webservice;

import com.mycompany.blockkbusterr.dto.LoginRequest;
import com.mycompany.blockkbusterr.dto.UserRequest;
import com.mycompany.blockkbusterr.dto.UserResponse;
import com.mycompany.blockkbusterr.entity.User;
import com.mycompany.blockkbusterr.service.UserService;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.xml.ws.soap.SOAPBinding;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@WebService(
    name = "UserManagementService",
    serviceName = "UserManagementService",
    targetNamespace = "http://webservice.blockkbusterr.mycompany.com/"
)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL)
@Stateless
public class UserManagementWebService {
    
    @Inject
    private UserService userService;
    
    /**
     * Register a new user
     */
    @WebMethod(operationName = "registerUser")
    @WebResult(name = "userResponse")
    public UserResponse registerUser(
            @WebParam(name = "userRequest") UserRequest request) {
        
        try {
            if (request == null) {
                return UserResponse.error("User request cannot be null");
            }
            
            User user = userService.registerUser(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getUsername(),
                request.getPassword()
            );
            
            return UserResponse.success(user, "User registered successfully");
            
        } catch (IllegalArgumentException e) {
            return UserResponse.error(e.getMessage());
        } catch (Exception e) {
            return UserResponse.error("Registration failed: " + e.getMessage());
        }
    }
    
    /**
     * Authenticate user login
     */
    @WebMethod(operationName = "authenticateUser")
    @WebResult(name = "userResponse")
    public UserResponse authenticateUser(
            @WebParam(name = "loginRequest") LoginRequest request) {
        
        try {
            if (request == null) {
                return UserResponse.error("Login request cannot be null");
            }
            
            Optional<User> userOpt = userService.authenticateUser(
                request.getUsername(),
                request.getPassword()
            );
            
            if (userOpt.isPresent()) {
                return UserResponse.success(userOpt.get(), "Authentication successful");
            } else {
                return UserResponse.error("Invalid username or password");
            }
            
        } catch (Exception e) {
            return UserResponse.error("Authentication failed: " + e.getMessage());
        }
    }
    
    /**
     * Get user profile by ID
     */
    @WebMethod(operationName = "getUserProfile")
    @WebResult(name = "userResponse")
    public UserResponse getUserProfile(
            @WebParam(name = "userId") Long userId) {
        
        try {
            if (userId == null) {
                return UserResponse.error("User ID cannot be null");
            }
            
            Optional<User> userOpt = userService.findUserById(userId);
            if (userOpt.isPresent()) {
                return UserResponse.success(userOpt.get());
            } else {
                return UserResponse.error("User not found");
            }
            
        } catch (Exception e) {
            return UserResponse.error("Failed to get user profile: " + e.getMessage());
        }
    }
    
    /**
     * Get user profile by username
     */
    @WebMethod(operationName = "getUserByUsername")
    @WebResult(name = "userResponse")
    public UserResponse getUserByUsername(
            @WebParam(name = "username") String username) {
        
        try {
            if (username == null || username.trim().isEmpty()) {
                return UserResponse.error("Username cannot be null or empty");
            }
            
            Optional<User> userOpt = userService.findUserByUsername(username);
            if (userOpt.isPresent()) {
                return UserResponse.success(userOpt.get());
            } else {
                return UserResponse.error("User not found");
            }
            
        } catch (Exception e) {
            return UserResponse.error("Failed to get user: " + e.getMessage());
        }
    }
    
    /**
     * Update user profile
     */
    @WebMethod(operationName = "updateUserProfile")
    @WebResult(name = "userResponse")
    public UserResponse updateUserProfile(
            @WebParam(name = "userId") Long userId,
            @WebParam(name = "firstName") String firstName,
            @WebParam(name = "lastName") String lastName,
            @WebParam(name = "email") String email) {
        
        try {
            if (userId == null) {
                return UserResponse.error("User ID cannot be null");
            }
            
            User user = userService.updateUserProfile(userId, firstName, lastName, email);
            return UserResponse.success(user, "Profile updated successfully");
            
        } catch (IllegalArgumentException e) {
            return UserResponse.error(e.getMessage());
        } catch (Exception e) {
            return UserResponse.error("Failed to update profile: " + e.getMessage());
        }
    }
    
    /**
     * Change user password
     */
    @WebMethod(operationName = "changePassword")
    @WebResult(name = "success")
    public boolean changePassword(
            @WebParam(name = "userId") Long userId,
            @WebParam(name = "currentPassword") String currentPassword,
            @WebParam(name = "newPassword") String newPassword) {
        
        try {
            if (userId == null) {
                return false;
            }
            
            return userService.changePassword(userId, currentPassword, newPassword);
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if username is available
     */
    @WebMethod(operationName = "isUsernameAvailable")
    @WebResult(name = "available")
    public boolean isUsernameAvailable(
            @WebParam(name = "username") String username) {
        
        try {
            return userService.isUsernameAvailable(username);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if email is available
     */
    @WebMethod(operationName = "isEmailAvailable")
    @WebResult(name = "available")
    public boolean isEmailAvailable(
            @WebParam(name = "email") String email) {
        
        try {
            return userService.isEmailAvailable(email);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get all users (admin function)
     */
    @WebMethod(operationName = "getAllUsers")
    @WebResult(name = "users")
    public List<UserResponse> getAllUsers() {
        
        try {
            List<User> users = userService.getAllUsers();
            return users.stream()
                    .map(user -> UserResponse.success(user))
                    .collect(Collectors.toList());
            
        } catch (Exception e) {
            return List.of(UserResponse.error("Failed to get users: " + e.getMessage()));
        }
    }
    
    /**
     * Search users by name
     */
    @WebMethod(operationName = "searchUsers")
    @WebResult(name = "users")
    public List<UserResponse> searchUsers(
            @WebParam(name = "searchTerm") String searchTerm) {
        
        try {
            List<User> users = userService.searchUsersByName(searchTerm);
            return users.stream()
                    .map(user -> UserResponse.success(user))
                    .collect(Collectors.toList());
            
        } catch (Exception e) {
            return List.of(UserResponse.error("Failed to search users: " + e.getMessage()));
        }
    }
    
    /**
     * Activate user (admin function)
     */
    @WebMethod(operationName = "activateUser")
    @WebResult(name = "success")
    public boolean activateUser(
            @WebParam(name = "userId") Long userId) {
        
        try {
            return userService.activateUser(userId);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Deactivate user (admin function)
     */
    @WebMethod(operationName = "deactivateUser")
    @WebResult(name = "success")
    public boolean deactivateUser(
            @WebParam(name = "userId") Long userId) {
        
        try {
            return userService.deactivateUser(userId);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Promote user to admin (admin function)
     */
    @WebMethod(operationName = "promoteToAdmin")
    @WebResult(name = "success")
    public boolean promoteToAdmin(
            @WebParam(name = "userId") Long userId) {
        
        try {
            return userService.promoteToAdmin(userId);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Demote user from admin (admin function)
     */
    @WebMethod(operationName = "demoteFromAdmin")
    @WebResult(name = "success")
    public boolean demoteFromAdmin(
            @WebParam(name = "userId") Long userId) {
        
        try {
            return userService.demoteFromAdmin(userId);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get user statistics (admin function)
     */
    @WebMethod(operationName = "getUserStats")
    @WebResult(name = "userStats")
    public String getUserStats() {
        
        try {
            UserService.UserStats stats = userService.getUserStats();
            return String.format("Total: %d, Active: %d, Admins: %d, Regular: %d",
                    stats.getTotalUsers(),
                    stats.getActiveUsers(),
                    stats.getAdminUsers(),
                    stats.getRegularUsers());
            
        } catch (Exception e) {
            return "Failed to get statistics: " + e.getMessage();
        }
    }
}