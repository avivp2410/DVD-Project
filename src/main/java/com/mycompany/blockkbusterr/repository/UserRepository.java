package com.mycompany.blockkbusterr.repository;

import com.mycompany.blockkbusterr.entity.User;
import com.mycompany.blockkbusterr.entity.UserRole;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRepository extends BaseRepository<User, Long> {
    
    public UserRepository() {
        super(User.class);
    }
    
    @Override
    protected Long getId(User entity) {
        return entity.getUserId();
    }
    
    /**
     * Find user by username
     */
    public Optional<User> findByUsername(String username) {
        try {
            TypedQuery<User> query = createNamedQuery("User.findByUsername");
            query.setParameter("username", username);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    
    /**
     * Find user by email
     */
    public Optional<User> findByEmail(String email) {
        try {
            TypedQuery<User> query = createNamedQuery("User.findByEmail");
            query.setParameter("email", email);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    
    /**
     * Authenticate user with username and password
     */
    public Optional<User> findByUsernameAndPassword(String username, String password) {
        try {
            TypedQuery<User> query = createNamedQuery("User.findByUsernameAndPassword");
            query.setParameter("username", username);
            query.setParameter("password", password);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    
    /**
     * Find users by role
     */
    public List<User> findByRole(UserRole role) {
        String jpql = "SELECT u FROM User u WHERE u.role = :role ORDER BY u.lastName, u.firstName";
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("role", role);
        return query.getResultList();
    }
    
    /**
     * Find active users
     */
    public List<User> findActiveUsers() {
        String jpql = "SELECT u FROM User u WHERE u.active = true ORDER BY u.lastName, u.firstName";
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        return query.getResultList();
    }
    
    /**
     * Search users by name (first name or last name)
     */
    public List<User> searchByName(String searchTerm) {
        String jpql = "SELECT u FROM User u WHERE " +
                     "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                     "LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
                     "ORDER BY u.lastName, u.firstName";
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("searchTerm", searchTerm);
        return query.getResultList();
    }
    
    /**
     * Check if username exists
     */
    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }
    
    /**
     * Check if email exists
     */
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }
    
    /**
     * Count users by role
     */
    public long countByRole(UserRole role) {
        String jpql = "SELECT COUNT(u) FROM User u WHERE u.role = :role";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("role", role);
        return query.getSingleResult();
    }
    
    /**
     * Count active users
     */
    public long countActiveUsers() {
        String jpql = "SELECT COUNT(u) FROM User u WHERE u.active = true";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        return query.getSingleResult();
    }
    
    /**
     * Update user password
     */
    public boolean updatePassword(Long userId, String newPassword) {
        String jpql = "UPDATE User u SET u.password = :password WHERE u.userId = :userId";
        int updatedRows = entityManager.createQuery(jpql)
                .setParameter("password", newPassword)
                .setParameter("userId", userId)
                .executeUpdate();
        return updatedRows > 0;
    }
    
    /**
     * Activate/Deactivate user
     */
    public boolean updateUserStatus(Long userId, boolean active) {
        String jpql = "UPDATE User u SET u.active = :active WHERE u.userId = :userId";
        int updatedRows = entityManager.createQuery(jpql)
                .setParameter("active", active)
                .setParameter("userId", userId)
                .executeUpdate();
        return updatedRows > 0;
    }
    
    /**
     * Find users with recent activity (users who have rentals)
     */
    public List<User> findUsersWithRentals() {
        String jpql = "SELECT DISTINCT u FROM User u " +
                     "JOIN u.rentals r " +
                     "WHERE u.active = true " +
                     "ORDER BY u.lastName, u.firstName";
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        return query.getResultList();
    }
}