# BlockKBusterr - Classes and Functions Documentation

## Project Overview
BlockKBusterr is a Java web application for DVD rental management built with Jakarta EE, JSF (JavaServer Faces), and JPA (Java Persistence API). The application follows a layered architecture pattern with clear separation of concerns.

## Architecture Hierarchy

```
BlockKBusterr Application
├── Configuration Layer
│   └── JakartaRestConfiguration
├── Presentation Layer (JSF Managed Beans)
│   ├── AdminBean
│   ├── AuthenticationBean
│   ├── DatabaseStartupBean
│   ├── MovieDetailsBean
│   ├── MovieListBean
│   ├── MovieManagementBean
│   ├── ProfileBean
│   ├── RentalBean
│   ├── SessionBean
│   └── UserRegistrationBean
├── Business Logic Layer (Services)
│   ├── DatabaseInitializationService
│   ├── MovieService
│   ├── RentalService
│   ├── ReviewService
│   └── UserService
├── Data Access Layer (Repositories)
│   ├── BaseRepository
│   ├── MovieRepository
│   ├── RentalRepository
│   ├── ReviewRepository
│   └── UserRepository
├── Domain Model Layer (Entities)
│   ├── Movie
│   ├── Rental
│   ├── RentalStatus (Enum)
│   ├── Review
│   ├── User
│   └── UserRole (Enum)
└── Utility Layer
    └── PasswordUtil
```

---

## 1. Configuration Layer

### 1.1 JakartaRestConfiguration
**Package:** `com.mycompany.blockkbusterr`

A simple configuration class that sets up Jakarta RESTful Web Services for the application.

#### Class Details:
- **Annotations:** `@ApplicationPath("resources")`
- **Extends:** `Application`
- **Purpose:** Configures the REST endpoint base path

---

## 2. Domain Model Layer (Entities)

### 2.1 Movie Entity
**Package:** `com.mycompany.blockkbusterr.entity`

Represents a movie in the DVD rental system.

#### Class Details:
- **Annotations:** `@Entity`
- **Implements:** `Serializable`

#### Fields:
- `id` (Long) - Primary key with auto-generation
- `title` (String) - Movie title (required, not blank)
- `releaseYear` (Integer) - Release year (required)
- `duration` (Integer) - Duration in minutes (required)
- `genre` (String) - Movie genre (required, not blank)
- `quantity` (Integer) - Available copies (required)
- `description` (String) - Movie description
- `createdAt` (LocalDateTime) - Creation timestamp
- `updatedAt` (LocalDateTime) - Last update timestamp
- `reviews` (List<Review>) - Associated reviews (One-to-Many)

#### Functions:
- **Constructor:** `Movie(String title, Integer releaseYear, Integer duration, String genre, Integer quantity)`
- **@PreUpdate:** `updateTimestamp()` - Updates the updatedAt field before database updates
- **getDurationFormatted()** - Returns formatted duration string (e.g., "2h 30min")
- **getAverageRating()** - Calculates average rating from reviews
- **equals()**, **hashCode()**, **toString()** - Standard object methods

### 2.2 User Entity
**Package:** `com.mycompany.blockkbusterr.entity`

Represents a user in the system (both regular users and administrators).

#### Fields:
- `id` (Long) - Primary key with auto-generation
- `firstName` (String) - User's first name (required, not blank)
- `lastName` (String) - User's last name (required, not blank)
- `email` (String) - User's email (required, not blank)
- `username` (String) - Unique username (required, not blank)
- `password` (String) - Hashed password (required, not blank)
- `role` (UserRole) - User role (USER or ADMIN)
- `active` (boolean) - Account status
- `createdAt` (LocalDateTime) - Creation timestamp
- `updatedAt` (LocalDateTime) - Last update timestamp

#### Functions:
- **Constructor:** `User(String firstName, String lastName, String email, String username, String password)`
- **@PreUpdate:** `updateTimestamp()` - Updates the updatedAt field
- **equals()**, **hashCode()**, **toString()** - Standard object methods

### 2.3 Rental Entity
**Package:** `com.mycompany.blockkbusterr.entity`

Represents a movie rental transaction.

#### Fields:
- `id` (Long) - Primary key with auto-generation
- `user` (User) - Renting user (Many-to-One, lazy fetch)
- `movie` (Movie) - Rented movie (Many-to-One, lazy fetch)
- `rentalDate` (LocalDate) - Rental start date
- `returnDate` (LocalDate) - Expected return date
- `actualReturnDate` (LocalDate) - Actual return date (nullable)
- `status` (RentalStatus) - Current rental status
- `createdAt` (LocalDateTime) - Creation timestamp
- `updatedAt` (LocalDateTime) - Last update timestamp

#### Functions:
- **Default Constructor:** `Rental()`
- **Constructor:** `Rental(User user, Movie movie, LocalDate returnDate)`
- **@PreUpdate:** `updateTimestamp()` - Updates the updatedAt field
- **getDaysRented()** - Calculates days since rental
- **getDaysOverdue()** - Calculates overdue days (if applicable)
- **markAsReturned()** - Marks rental as returned with current date
- **equals()**, **hashCode()**, **toString()** - Standard object methods

### 2.4 Review Entity
**Package:** `com.mycompany.blockkbusterr.entity`

Represents user reviews for movies.

#### Fields:
- `id` (Long) - Primary key with auto-generation
- `user` (User) - Reviewing user (Many-to-One, lazy fetch)
- `movie` (Movie) - Reviewed movie (Many-to-One, lazy fetch)
- `rating` (Integer) - Rating (1-5, required)
- `comment` (String) - Optional review comment
- `createdAt` (LocalDateTime) - Creation timestamp
- `updatedAt` (LocalDateTime) - Last update timestamp

#### Functions:
- **Constructor:** `Review(User user, Movie movie, Integer rating)`
- **Constructor:** `Review(User user, Movie movie, Integer rating, String comment)`
- **@PreUpdate:** `updateTimestamp()` - Updates the updatedAt field
- **getRatingStars()** - Returns star representation of rating (e.g., "★★★★☆")
- **getShortComment(int maxLength)** - Returns truncated comment
- **equals()**, **hashCode()**, **toString()** - Standard object methods

### 2.5 RentalStatus Enum
**Package:** `com.mycompany.blockkbusterr.entity`

#### Values:
- `ACTIVE("Active")` - Currently rented
- `RETURNED("Returned")` - Successfully returned
- `OVERDUE("Overdue")` - Past due date
- `CANCELLED("Cancelled")` - Rental cancelled

#### Functions:
- **toString()** - Returns display name

### 2.6 UserRole Enum
**Package:** `com.mycompany.blockkbusterr.entity`

#### Values:
- `USER("User")` - Regular user
- `ADMIN("Administrator")` - Administrator

#### Functions:
- **toString()** - Returns display name

---

## 3. Data Access Layer (Repositories)

### 3.1 BaseRepository<T, ID>
**Package:** `com.mycompany.blockkbusterr.repository`

Abstract base repository providing common CRUD operations for all entities.

#### Functions:
- **save(T entity)** - Persists new entity
- **saveOrUpdate(T entity)** - Persists or merges entity
- **findById(ID id)** - Finds entity by ID
- **findAll()** - Returns all entities
- **deleteById(ID id)** - Deletes entity by ID
- **delete(T entity)** - Deletes entity
- **count()** - Returns total entity count

### 3.2 MovieRepository
**Package:** `com.mycompany.blockkbusterr.repository`
**Extends:** `BaseRepository<Movie, Long>`

#### Search Functions:
- **findByTitle(String title)** - Finds movies by title (partial match)
- **findByGenre(String genre)** - Finds movies by genre
- **findAvailableMovies()** - Finds movies with quantity > 0
- **findByReleaseYear(int year)** - Finds movies by release year
- **findByReleaseYearRange(int startYear, int endYear)** - Finds movies in year range
- **findByDurationRange(int minDuration, int maxDuration)** - Finds movies by duration
- **searchMovies(String searchTerm)** - Full-text search in title and genre
- **findLowStockMovies(int threshold)** - Finds movies below stock threshold
- **findOutOfStockMovies()** - Finds movies with zero quantity
- **findNewestMovies(int limit)** - Finds newest movies
- **findMostPopularMovies(int limit)** - Finds most rented movies
- **findByMinimumRating(double minRating)** - Finds movies with minimum rating

#### Inventory Management Functions:
- **updateQuantity(Long movieId, int newQuantity)** - Updates movie quantity
- **decreaseQuantity(Long movieId)** - Decreases quantity by 1
- **increaseQuantity(Long movieId)** - Increases quantity by 1

#### Statistics Functions:
- **countByGenre(String genre)** - Counts movies by genre
- **countAvailableMovies()** - Counts available movies
- **getDistinctGenres()** - Returns list of all genres

### 3.3 UserRepository
**Package:** `com.mycompany.blockkbusterr.repository`
**Extends:** `BaseRepository<User, Long>`

#### Authentication Functions:
- **findByUsername(String username)** - Finds user by username
- **findByEmail(String email)** - Finds user by email
- **findByUsernameAndPassword(String username, String password)** - Authentication lookup

#### User Management Functions:
- **findByRole(UserRole role)** - Finds users by role
- **findActiveUsers()** - Finds active users only
- **searchByName(String searchTerm)** - Searches users by first/last name
- **updatePassword(Long userId, String newPassword)** - Updates user password
- **updateUserStatus(Long userId, boolean active)** - Updates user active status
- **findUsersWithRentals()** - Finds users who have rental history

#### Statistics Functions:
- **countByRole(UserRole role)** - Counts users by role
- **countActiveUsers()** - Counts active users

### 3.4 RentalRepository
**Package:** `com.mycompany.blockkbusterr.repository`
**Extends:** `BaseRepository<Rental, Long>`

#### User Rental Functions:
- **findByUser(User user)** - Finds all rentals for user
- **findByUserId(Long userId)** - Finds rentals by user ID
- **findActiveRentalsByUser(User user)** - Finds active rentals for user
- **findActiveRentalsByUserId(Long userId)** - Finds active rentals by user ID
- **hasActiveRental(Long userId, Long movieId)** - Checks if user has active rental for movie
- **countActiveRentalsByUser(Long userId)** - Counts active rentals per user

#### Movie Rental Functions:
- **findByMovie(Movie movie)** - Finds rentals for specific movie
- **findByMovieId(Long movieId)** - Finds rentals by movie ID
- **getRentalHistoryByMovie(Long movieId, int limit)** - Gets rental history for movie

#### Status-Based Functions:
- **findByStatus(RentalStatus status)** - Finds rentals by status
- **findActiveRentals()** - Finds all active rentals
- **findOverdueRentals()** - Finds overdue rentals
- **updateRentalStatus(Long rentalId, RentalStatus status)** - Updates rental status
- **markAsReturned(Long rentalId)** - Marks rental as returned

#### Date-Based Functions:
- **findByDateRange(LocalDate startDate, LocalDate endDate)** - Rentals in date range
- **findDueOnDate(LocalDate date)** - Rentals due on specific date
- **findDueWithinDays(int days)** - Rentals due within specified days
- **findRecentRentals(int days)** - Recent rentals within days

#### Statistics Functions:
- **countByStatus(RentalStatus status)** - Counts rentals by status
- **countOverdueRentals()** - Counts overdue rentals
- **getUserRentalStats(Long userId)** - Gets rental statistics for user

### 3.5 ReviewRepository
**Package:** `com.mycompany.blockkbusterr.repository`
**Extends:** `BaseRepository<Review, Long>`

#### Basic Query Functions:
- **findByMovie(Movie movie)** - Finds reviews for movie
- **findByMovieId(Long movieId)** - Finds reviews by movie ID
- **findByUser(User user)** - Finds reviews by user
- **findByUserId(Long userId)** - Finds reviews by user ID
- **findByRating(Integer rating)** - Finds reviews by rating
- **findByUserAndMovie(User user, Movie movie)** - Finds review by user and movie
- **findByUserIdAndMovieId(Long userId, Long movieId)** - Finds review by IDs

#### Rating-Based Functions:
- **findByRatingRange(int minRating, int maxRating)** - Finds reviews in rating range
- **findTopRatedReviewsForMovie(Long movieId, int limit)** - Top-rated reviews for movie
- **getAverageRatingForMovie(Long movieId)** - Calculates average rating
- **getRatingDistributionForMovie(Long movieId)** - Gets rating distribution

#### Time-Based Functions:
- **findRecentReviews(int days)** - Recent reviews within days
- **findRecentReviewsLimited(int limit)** - Limited recent reviews

#### Comment-Based Functions:
- **findReviewsWithComments()** - Reviews that have comments
- **findReviewsWithCommentsForMovie(Long movieId)** - Reviews with comments for movie

#### Management Functions:
- **updateReview(Long reviewId, Integer rating, String comment)** - Updates existing review
- **softDeleteReview(Long reviewId)** - Soft deletes review
- **reactivateReview(Long reviewId)** - Reactivates soft-deleted review

#### Statistics Functions:
- **countReviewsForMovie(Long movieId)** - Counts reviews for movie
- **countReviewsByUser(Long userId)** - Counts reviews by user
- **countReviewsByRating(Integer rating)** - Counts reviews by rating
- **findMostHelpfulReviews(int limit)** - Finds most helpful reviews

---

## 4. Business Logic Layer (Services)

### 4.1 DatabaseInitializationService
**Package:** `com.mycompany.blockkbusterr.service`

Handles automatic database initialization with sample data.

#### Functions:
- **@Observes onApplicationStart()** - Triggered on application startup
- **initializeDatabase()** - Main initialization method (@Transactional)
- **initializeAdminUser()** - Creates default admin user
- **initializeSampleMovies()** - Creates sample movie data
- **createMovie()** - Helper to create individual movies
- **shouldInitialize()** - Checks if initialization is needed

### 4.2 MovieService
**Package:** `com.mycompany.blockkbusterr.service`

Core business logic for movie operations.

#### Movie Management Functions:
- **addMovie()** - Adds new movie with validation
- **updateMovie()** - Updates existing movie
- **findMovieById(Long movieId)** - Retrieves movie by ID
- **searchMovies(String searchTerm)** - Searches movies
- **updateMovieQuantity(Long movieId, int newQuantity)** - Updates inventory

#### Availability Functions:
- **isMovieAvailable(Long movieId)** - Checks if movie is available for rent

#### Statistics Functions:
- **getMovieStats()** - Returns MovieStats object with total, available, low stock, and out of stock counts
- **getMovieWithRating(Long movieId)** - Returns MovieWithRating object

#### Inner Classes:
- **MovieStats** - Data transfer object for movie statistics
  - **Constructor:** `MovieStats(long totalMovies, long availableMovies, long lowStockMovies, long outOfStockMovies)`
- **MovieWithRating** - Movie with average rating data
  - **Constructor:** `MovieWithRating(Movie movie, double averageRating, long reviewCount)`

### 4.3 RentalService
**Package:** `com.mycompany.blockkbusterr.service`

Core business logic for rental operations.

#### Rental Management Functions:
- **createRental(Long userId, Long movieId, LocalDate returnDate)** - Creates new rental with validation
- **returnRental(Long rentalId)** - Processes movie return
- **extendRental(Long rentalId, LocalDate newReturnDate)** - Extends rental period
- **cancelRental(Long rentalId)** - Cancels active rental

#### Validation Functions:
- **canUserRentMovie(Long userId, Long movieId)** - Validates if user can rent specific movie

#### Statistics Functions:
- **getRentalStats()** - Returns RentalStats object

#### Inner Classes:
- **RentalStats** - Data transfer object for rental statistics
  - **Constructor:** `RentalStats(long totalRentals, long activeRentals, long overdueRentals, long returnedRentals)`

### 4.4 ReviewService
**Package:** `com.mycompany.blockkbusterr.service`

Core business logic for review operations.

#### Review Management Functions:
- **addReview(Long userId, Long movieId, Integer rating, String comment)** - Creates new review
- **updateReview(Long reviewId, Integer rating, String comment)** - Updates existing review

#### Rating Functions:
- **getAverageRatingForMovie(Long movieId)** - Calculates average rating
- **getRatingDistributionForMovie(Long movieId)** - Gets rating distribution

#### Validation Functions:
- **canUserReviewMovie(Long userId, Long movieId)** - Validates if user can review movie

#### Summary Functions:
- **getMovieReviewSummary(Long movieId)** - Gets comprehensive movie review data
- **getUserReviewSummary(Long userId)** - Gets user's review statistics
- **getReviewStats()** - Gets system-wide review statistics

#### Inner Classes:
- **RatingDistribution** - Rating count data
  - **Constructor:** `RatingDistribution(int rating, long count)`
- **MovieReviewSummary** - Comprehensive movie review data
  - **Constructor:** `MovieReviewSummary(Long movieId, double averageRating, long totalReviews, List<RatingDistribution> ratingDistribution)`
- **UserReviewSummary** - User review statistics
  - **Constructor:** `UserReviewSummary(Long userId, long totalReviews, double averageRating)`
- **ReviewStats** - System-wide review statistics
  - **Constructor:** `ReviewStats(long totalReviews, long fiveStarReviews, long fourStarReviews, ...)`
  - **getAverageRating()** - Calculates overall system rating average

### 4.5 UserService
**Package:** `com.mycompany.blockkbusterr.service`

Core business logic for user operations.

#### User Management Functions:
- **registerUser()** - Registers new user with validation and password hashing
- **authenticateUser(String username, String password)** - Authenticates user login
- **findUserByUsername(String username)** - Retrieves user by username
- **findUserByEmail(String email)** - Retrieves user by email
- **searchUsersByName(String searchTerm)** - Searches users by name
- **updateUserProfile()** - Updates user profile information

#### Password Functions:
- **changePassword(Long userId, String currentPassword, String newPassword)** - Changes user password with validation
- **resetPassword(Long userId, String newPassword)** - Resets password (admin function)

#### Role Management Functions:
- **promoteToAdmin(Long userId)** - Promotes user to admin role
- **demoteFromAdmin(Long userId)** - Demotes admin to regular user

#### Validation Functions:
- **isUsernameAvailable(String username)** - Checks username availability
- **isEmailAvailable(String email)** - Checks email availability

#### Statistics Functions:
- **getUserStats()** - Returns UserStats object

#### Inner Classes:
- **UserStats** - Data transfer object for user statistics
  - **Constructor:** `UserStats(long totalUsers, long activeUsers, long adminUsers, long regularUsers)`

---

## 5. Presentation Layer (JSF Managed Beans)

### 5.1 AdminBean
**Package:** `com.mycompany.blockkbusterr.bean`
**Annotations:** `@Named("adminBean")`, `@ViewScoped`

Administrative dashboard and management functions.

#### Initialization Functions:
- **@PostConstruct init()** - Initializes bean and loads dashboard data
- **loadDashboardData()** - Loads all dashboard components
- **loadRecentRentals()** - Loads recent rental data
- **loadOverdueRentals()** - Loads overdue rentals
- **loadLowStockMovies()** - Loads low inventory movies
- **loadStats()** - Loads system statistics
- **loadUsers()** - Loads user list
- **loadAllMovies()** - Loads movie catalog

#### Search Functions:
- **searchUsers()** - Searches users based on criteria

#### Rental Management Functions:
- **processReturn(Long rentalId)** - Processes movie return
- **getUserRentalHistory(Long userId)** - Gets user's rental history

#### Movie Management Functions:
- **updateMovieQuantity(Long movieId, int newQuantity)** - Updates movie inventory
- **deleteMovie(Long movieId)** - Deletes movie from catalog

#### UI Helper Functions:
- **getRentalStatusClass(Rental rental)** - Returns CSS class for rental status
- **getStockLevelClass(Movie movie)** - Returns CSS class for stock level
- **getRentalDuration(Rental rental)** - Formats rental duration display
- **formatDate()** - Formats dates for display

#### Message Functions:
- **addSuccessMessage(String message)** - Adds success message
- **addErrorMessage(String message)** - Adds error message

### 5.2 AuthenticationBean
**Package:** `com.mycompany.blockkbusterr.bean`
**Annotations:** `@Named("authBean")`, `@ViewScoped`

Handles user authentication and login.

#### Authentication Functions:
- **login()** - Processes user login and navigation
- **checkAlreadyLoggedIn()** - Redirects if user already authenticated

#### Validation Functions:
- **isFormValid()** - Validates login form completeness

#### UI Helper Functions:
- **getLoginButtonClass()** - Returns CSS class for login button
- **isLoginButtonDisabled()** - Determines if login button should be disabled
- **clearForm()** - Clears login form fields

#### Message Functions:
- **addErrorMessage()**, **addInfoMessage()**, **addWarningMessage()** - Message handling

### 5.3 DatabaseStartupBean
**Package:** `com.mycompany.blockkbusterr.bean`
**Annotations:** `@Singleton`, `@Startup`

Manages database initialization on application startup.

#### Initialization Functions:
- **@PostConstruct init()** - Handles startup database initialization
- **reinitializeDatabase()** - Manually reinitializes database

### 5.4 MovieDetailsBean
**Package:** `com.mycompany.blockkbusterr.bean`
**Annotations:** `@Named`, `@ViewScoped`

Manages movie detail page functionality.

#### Initialization Functions:
- **@PostConstruct init()** - Initializes movie details from parameter
- **loadMovieDetails(Long movieId)** - Loads movie and review data
- **checkIfUserHasReviewed()** - Checks if current user has reviewed movie

#### Rental Functions:
- **rentMovie()** - Processes movie rental request

#### Review Functions:
- **submitReview()** - Submits new movie review

#### UI Helper Functions:
- **getAverageRatingFormatted()** - Formats average rating for display
- **getRatingStarsDisplay(int rating)** - Returns star representation
- **addMessage()** - Adds JSF messages

### 5.5 MovieListBean
**Package:** `com.mycompany.blockkbusterr.bean`
**Annotations:** `@Named("movieListBean")`, `@ViewScoped`

Manages movie listing and search functionality.

#### Initialization Functions:
- **@PostConstruct init()** - Loads movies and genres
- **loadMovies()** - Loads movie catalog
- **loadGenres()** - Loads available genres

#### Search and Filter Functions:
- **searchMovies()** - Executes movie search
- **clearFilters()** - Resets search filters
- **toggleAvailabilityFilter()** - Toggles availability filter
- **onGenreChange()** - Handles genre selection change

#### Data Retrieval Functions:
- **getNewestMovies()** - Gets newest movies
- **getMostPopularMovies()** - Gets most popular movies
- **getFilteredMovies()** - Returns filtered movie list

#### UI Helper Functions:
- **getAvailabilityStatus(Movie movie)** - Returns availability status text
- **getAvailabilityClass(Movie movie)** - Returns CSS class for availability

### 5.6 MovieManagementBean
**Package:** `com.mycompany.blockkbusterr.bean`
**Annotations:** `@Named("movieManagementBean")`, `@ViewScoped`

Handles movie addition and editing functionality.

#### Initialization Functions:
- **@PostConstruct init()** - Initializes bean based on mode (add/edit)
- **loadMovieForEdit()** - Loads movie data for editing

#### Management Functions:
- **addMovie()** - Adds new movie to catalog
- **updateMovie()** - Updates existing movie

#### Utility Functions:
- **clearForm()** - Resets form fields
- **addSuccessMessage()**, **addErrorMessage()** - Message handling

### 5.7 ProfileBean
**Package:** `com.mycompany.blockkbusterr.bean`
**Annotations:** `@Named`, `@ViewScoped`

Manages user profile viewing and editing.

#### Initialization Functions:
- **@PostConstruct init()** - Loads user profile data
- **loadUserProfile()** - Loads current user's profile
- **resetEditForm()** - Resets edit form to current values

#### Profile Management Functions:
- **toggleEditMode()** - Switches between view/edit mode
- **cancelEdit()** - Cancels edit operation
- **updateProfile()** - Updates user profile information

#### Statistics Functions:
- **getActiveRentals()** - Returns count of active rentals
- **getMemberSince()** - Returns formatted member since date

#### Utility Functions:
- **addMessage()** - Adds JSF messages

### 5.8 RentalBean
**Package:** `com.mycompany.blockkbusterr.bean`
**Annotations:** `@Named("rentalBean")`, `@ViewScoped`

Comprehensive rental management functionality.

#### Initialization Functions:
- **@PostConstruct init()** - Initializes rental interface
- **loadSelectedMovie()** - Loads movie for rental
- **loadUserRentals()** - Loads current user's rentals
- **loadAllRentals()** - Loads all rentals (admin)
- **loadOverdueRentals()** - Loads overdue rentals

#### Rental Management Functions:
- **createRental()** - Creates new rental
- **quickRentMovie(Long movieId)** - Quick rental from movie list
- **extendRental(Long rentalId)** - Extends rental period
- **userReturnMovie(Long rentalId)** - User returns movie
- **returnMovie(Long rentalId)** - Admin processes return

#### Filter and Search Functions:
- **filterRentals()** - Filters rental list

#### Validation Functions:
- **canRentSelectedMovie()** - Validates rental eligibility

#### UI Helper Functions:
- **getRentalPeriod()** - Returns formatted rental period
- **getStatusClass(Rental rental)** - Returns CSS class for status
- **viewMovieDetails(Long movieId)** - Navigation to movie details
- **getFormattedReturnDate()** - Formats return date
- **getActiveRentalCount()** - Returns active rental count

#### Utility Functions:
- **clearForm()** - Resets rental form
- **addSuccessMessage()**, **addErrorMessage()** - Message handling

### 5.9 SessionBean
**Package:** `com.mycompany.blockkbusterr.bean`
**Annotations:** `@Named("sessionBean")`, `@SessionScoped`

Manages user session and authentication state.

#### Session Management Functions:
- **setCurrentUser(User user)** - Sets current authenticated user
- **logout()** - Handles user logout

#### User Information Functions:
- **getCurrentUserDisplayName()** - Returns user's display name
- **getCurrentUsername()** - Returns current username
- **getCurrentUserInitials()** - Returns user initials for avatar

#### Security Functions:
- **requireAdmin()** - Enforces admin access requirement
- **requireAuthentication()** - Enforces authentication requirement
- **canAccessUserData(Long userId)** - Validates user data access
- **checkAccess(String requiredRole)** - Role-based access control

#### Navigation Functions:
- **getHomePageForCurrentUser()** - Returns appropriate home page

#### Session State Functions:
- **isSessionNearExpiry()** - Checks if session is near expiration

#### Utility Functions:
- **addMessage()** - Adds JSF messages

### 5.10 UserRegistrationBean
**Package:** `com.mycompany.blockkbusterr.bean`
**Annotations:** `@Named("registrationBean")`, `@ViewScoped`

Handles new user registration functionality.

#### Registration Functions:
- **register()** - Processes new user registration

#### Validation Functions:
- **checkUsernameAvailability()** - AJAX validation for username
- **checkEmailAvailability()** - AJAX validation for email
- **validatePasswordConfirmation()** - AJAX password confirmation validation
- **validateForm()** - Server-side form validation
- **isFormValid()** - Checks overall form validity

#### UI Helper Functions:
- **isRegisterButtonDisabled()** - Determines if register button should be disabled

#### Utility Functions:
- **clearForm()** - Resets registration form
- **addErrorMessage()**, **addSuccessMessage()** - Message handling
- **addFieldError()** - Adds field-specific error messages

---

## 6. Utility Layer

### 6.1 PasswordUtil
**Package:** `com.mycompany.blockkbusterr.util`

Utility class for password operations.

#### Functions:
- **isValidPassword(String password)** - Validates password strength requirements

---

## Summary

The BlockKBusterr application demonstrates a well-structured Java web application with:

- **27 Classes** total across all layers
- **Entity Layer:** 6 classes (4 entities + 2 enums)
- **Repository Layer:** 5 classes (1 base + 4 specific repositories)
- **Service Layer:** 5 classes providing business logic
- **Presentation Layer:** 10 JSF managed beans
- **Utility Layer:** 1 utility class
- **Configuration:** 1 configuration class

Each layer has distinct responsibilities:
- **Entities** define the domain model
- **Repositories** handle data persistence
- **Services** implement business rules
- **Beans** manage user interface logic
- **Utilities** provide common functionality

The application supports complete DVD rental operations including user management, movie catalog management, rental processing, review system, and administrative functions.