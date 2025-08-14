package com.mycompany.blockkbusterr.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "rentals")
@NamedQueries({
    @NamedQuery(name = "Rental.findAll", query = "SELECT r FROM Rental r ORDER BY r.borrowDate DESC"),
    @NamedQuery(name = "Rental.findByUser", query = "SELECT r FROM Rental r WHERE r.user = :user ORDER BY r.borrowDate DESC"),
    @NamedQuery(name = "Rental.findByMovie", query = "SELECT r FROM Rental r WHERE r.movie = :movie ORDER BY r.borrowDate DESC"),
    @NamedQuery(name = "Rental.findByStatus", query = "SELECT r FROM Rental r WHERE r.status = :status ORDER BY r.borrowDate DESC"),
    @NamedQuery(name = "Rental.findActiveRentals", query = "SELECT r FROM Rental r WHERE r.status = 'ACTIVE' ORDER BY r.returnDate ASC"),
    @NamedQuery(name = "Rental.findOverdueRentals", query = "SELECT r FROM Rental r WHERE r.status = 'ACTIVE' AND r.returnDate < CURRENT_DATE")
})
public class Rental implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rental_id")
    private Long rentalId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    @NotNull(message = "Movie is required")
    private Movie movie;
    
    @NotNull(message = "Borrow date is required")
    @Column(name = "borrow_date", nullable = false)
    private LocalDate borrowDate;
    
    @NotNull(message = "Return date is required")
    @Column(name = "return_date", nullable = false)
    private LocalDate returnDate;
    
    @Column(name = "actual_return_date")
    private LocalDate actualReturnDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RentalStatus status = RentalStatus.ACTIVE;
    
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;
    
    @Column(name = "notes", length = 500)
    private String notes;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public Rental() {
        this.createdAt = LocalDateTime.now();
        this.borrowDate = LocalDate.now();
    }
    
    public Rental(User user, Movie movie, LocalDate returnDate) {
        this();
        this.user = user;
        this.movie = movie;
        this.returnDate = returnDate;
    }
    
    public Rental(User user, Movie movie, LocalDate returnDate, String phoneNumber) {
        this(user, movie, returnDate);
        this.phoneNumber = phoneNumber;
    }
    
    // Lifecycle callbacks
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getRentalId() {
        return rentalId;
    }
    
    public void setRentalId(Long rentalId) {
        this.rentalId = rentalId;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Movie getMovie() {
        return movie;
    }
    
    public void setMovie(Movie movie) {
        this.movie = movie;
    }
    
    public LocalDate getBorrowDate() {
        return borrowDate;
    }
    
    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }
    
    public LocalDate getReturnDate() {
        return returnDate;
    }
    
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
    
    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }
    
    public void setActualReturnDate(LocalDate actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }
    
    public RentalStatus getStatus() {
        return status;
    }
    
    public void setStatus(RentalStatus status) {
        this.status = status;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Utility methods
    public boolean isOverdue() {
        return status == RentalStatus.ACTIVE && returnDate.isBefore(LocalDate.now());
    }
    
    public boolean isActive() {
        return status == RentalStatus.ACTIVE;
    }
    
    public boolean isReturned() {
        return status == RentalStatus.RETURNED;
    }
    
    public long getDaysRented() {
        LocalDate endDate = actualReturnDate != null ? actualReturnDate : LocalDate.now();
        return borrowDate.until(endDate).getDays();
    }
    
    public long getDaysOverdue() {
        if (!isOverdue()) {
            return 0;
        }
        return returnDate.until(LocalDate.now()).getDays();
    }
    
    public void markAsReturned() {
        this.status = RentalStatus.RETURNED;
        this.actualReturnDate = LocalDate.now();
    }
    
    // toString, equals, and hashCode
    @Override
    public String toString() {
        return "Rental{" +
                "rentalId=" + rentalId +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", movie=" + (movie != null ? movie.getTitle() : "null") +
                ", borrowDate=" + borrowDate +
                ", returnDate=" + returnDate +
                ", status=" + status +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rental)) return false;
        Rental rental = (Rental) o;
        return rentalId != null && rentalId.equals(rental.rentalId);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}