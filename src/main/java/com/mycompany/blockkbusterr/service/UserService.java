package com.mycompany.blockkbusterr.service;

import com.mycompany.blockkbusterr.entity.User;
import com.mycompany.blockkbusterr.entity.UserRole;
import com.mycompany.blockkbusterr.repository.UserRepository;
import com.mycompany.blockkbusterr.util.PasswordUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class UserService {
    
    @Inject
    private UserRepository userRepository;
    
    /**
     * Register a new user
     */
    public User registerUser(String firstName, String lastName, String email, String username, String password) {
        // Validate input
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (!PasswordUtil.isValidPassword(password)) {
            throw new IllegalArgumentException("Password must be at least 6 characters and contain both letters and numbers");
        }
        
        // Check if username already exists
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        // Create new user
        User user = new User();
        user.setFirstName(firstName.trim());
        user.setLastName(lastName.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setUsername(username.trim().toLowerCase());
        user.setPassword(PasswordUtil.hashPassword(password));
        user.setRole(UserRole.USER);
        user.setActive(true);
        
        return userRepository.save(user);
    }
    
    /**
     * Authenticate user
     */
    public Optional<User> authenticateUser(String username, String password) {
        if (username == null || password == null) {
            return Optional.empty();
        }
        
        Optional<User> userOpt = userRepository.findByUsername(username.trim().toLowerCase());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getActive() && PasswordUtil.verifyPassword(password, user.getPassword())) {
                return Optional.of(user);
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Find user by ID
     */
    public Optional<User> findUserById(Long userId) {
        return userRepository.findById(userId);
    }
    
    /**
     * Find user by username
     */
    public Optional<User> findUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByUsername(username.trim().toLowerCase());
    }
    
    /**
     * Find user by email
     */
    public Optional<User> findUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByEmail(email.trim().toLowerCase());
    }
    
    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Get active users
     */
    public List<User> getActiveUsers() {
        return userRepository.findActiveUsers();
    }
    
    /**
     * Search users by name
     */
    public List<User> searchUsersByName(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getActiveUsers();
        }
        return userRepository.searchByName(searchTerm.trim());
    }
    
    /**
     * Get users by role
     */
    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }
    
    /**
     * Update user profile
     */
    public User updateUserProfile(Long userId, String firstName, String lastName, String email) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        
        User user = userOpt.get();
        
        // Validate input
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        
        // Check if email is already taken by another user
        String newEmail = email.trim().toLowerCase();
        if (!user.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        // Update user fields
        user.setFirstName(firstName.trim());
        user.setLastName(lastName.trim());
        user.setEmail(newEmail);
        
        return userRepository.update(user);
    }
    
    /**
     * Change user password
     */
    public boolean changePassword(Long userId, String currentPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        
        // Verify current password
        if (!PasswordUtil.verifyPassword(currentPassword, user.getPassword())) {
            return false;
        }
        
        // Validate new password
        if (!PasswordUtil.isValidPassword(newPassword)) {
            throw new IllegalArgumentException("Password must be at least 6 characters and contain both letters and numbers");
        }
        
        // Update password
        String hashedPassword = PasswordUtil.hashPassword(newPassword);
        return userRepository.updatePassword(userId, hashedPassword);
    }
    
    /**
     * Reset user password (admin function)
     */
    public boolean resetPassword(Long userId, String newPassword) {
        if (!PasswordUtil.isValidPassword(newPassword)) {
            throw new IllegalArgumentException("Password must be at least 6 characters and contain both letters and numbers");
        }
        
        String hashedPassword = PasswordUtil.hashPassword(newPassword);
        return userRepository.updatePassword(userId, hashedPassword);
    }
    
    /**
     * Activate user
     */
    public boolean activateUser(Long userId) {
        return userRepository.updateUserStatus(userId, true);
    }
    
    /**
     * Deactivate user
     */
    public boolean deactivateUser(Long userId) {
        return userRepository.updateUserStatus(userId, false);
    }
    
    /**
     * Promote user to admin
     */
    public boolean promoteToAdmin(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        user.setRole(UserRole.ADMIN);
        userRepository.update(user);
        return true;
    }
    
    /**
     * Demote user from admin
     */
    public boolean demoteFromAdmin(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        user.setRole(UserRole.USER);
        userRepository.update(user);
        return true;
    }
    
    /**
     * Delete user
     */
    public boolean deleteUser(Long userId) {
        return userRepository.deleteById(userId);
    }
    
    /**
     * Check if username is available
     */
    public boolean isUsernameAvailable(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return !userRepository.existsByUsername(username.trim().toLowerCase());
    }
    
    /**
     * Check if email is available
     */
    public boolean isEmailAvailable(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return !userRepository.existsByEmail(email.trim().toLowerCase());
    }
    
    /**
     * Get user statistics
     */
    public UserStats getUserStats() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countActiveUsers();
        long adminUsers = userRepository.countByRole(UserRole.ADMIN);
        long regularUsers = userRepository.countByRole(UserRole.USER);
        
        return new UserStats(totalUsers, activeUsers, adminUsers, regularUsers);
    }
    
    /**
     * Get users with rental activity
     */
    public List<User> getUsersWithRentals() {
        return userRepository.findUsersWithRentals();
    }
    
    // Inner class for user statistics
    public static class UserStats {
        private final long totalUsers;
        private final long activeUsers;
        private final long adminUsers;
        private final long regularUsers;
        
        public UserStats(long totalUsers, long activeUsers, long adminUsers, long regularUsers) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
            this.adminUsers = adminUsers;
            this.regularUsers = regularUsers;
        }
        
        public long getTotalUsers() { return totalUsers; }
        public long getActiveUsers() { return activeUsers; }
        public long getAdminUsers() { return adminUsers; }
        public long getRegularUsers() { return regularUsers; }
        public long getInactiveUsers() { return totalUsers - activeUsers; }
    }
}