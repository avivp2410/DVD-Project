# BlockKBusterr - Technical Documentation

## Overview
BlockKBusterr is a movie rental system built using Jakarta EE (formerly Java EE) with JSF (JavaServer Faces) for the frontend. The application follows a layered architecture pattern with clear separation of concerns across Entity, Repository, Service, and Bean layers.

## Architecture Overview

The application follows a classic n-tier architecture:

```
Presentation Layer (JSF Beans) -> Service Layer -> Repository Layer -> Entity Layer (Database)
```

**Design Patterns Used:**
- Repository Pattern for data access abstraction
- Service Layer Pattern for business logic encapsulation
- Managed Bean Pattern for JSF integration
- Dependency Injection with CDI (Contexts and Dependency Injection)

---

## Entity Classes (Data Models)

### [`User.java`](src/main/java/com/mycompany/blockkbusterr/entity/User.java)
**Purpose:** Core entity representing system users with authentication and role management.
**Key Features:** JPA entity with Bean Validation, supports both regular users and administrators.
**Relationships:** One-to-many with [`Rental`](src/main/java/com/mycompany/blockkbusterr/entity/Rental.java) and [`Review`](src/main/java/com/mycompany/blockkbusterr/entity/Review.java) entities.
**Design Pattern:** Entity with automatic timestamp management using `@PreUpdate`.

### [`Movie.java`](src/main/java/com/mycompany/blockkbusterr/entity/Movie.java)
**Purpose:** Represents movie inventory with availability tracking and rating aggregation.
**Key Features:** Complex business logic for availability calculation, formatted duration display, and average rating computation.
**Relationships:** One-to-many with [`Rental`](src/main/java/com/mycompany/blockkbusterr/entity/Rental.java) and [`Review`](src/main/java/com/mycompany/blockkbusterr/entity/Review.java) entities for tracking rentals and reviews.
**Business Logic:** Built-in methods for stock management and rating calculations.

### [`Rental.java`](src/main/java/com/mycompany/blockkbusterr/entity/Rental.java)
**Purpose:** Manages the rental transaction lifecycle between users and movies.
**Key Features:** State management through [`RentalStatus`](src/main/java/com/mycompany/blockkbusterr/entity/RentalStatus.java) enum, automatic date calculations for overdue tracking.
**Relationships:** Many-to-one relationships with both [`User`](src/main/java/com/mycompany/blockkbusterr/entity/User.java) and [`Movie`](src/main/java/com/mycompany/blockkbusterr/entity/Movie.java) entities.
**Business Methods:** Includes duration calculation, overdue detection, and rental state transitions.

### [`Review.java`](src/main/java/com/mycompany/blockkbusterr/entity/Review.java)
**Purpose:** Handles user ratings and comments for movies with validation and formatting.
**Key Features:** Rating validation (1-5 stars), star display formatting, and comment truncation for UI display.
**Relationships:** Many-to-one relationships with [`User`](src/main/java/com/mycompany/blockkbusterr/entity/User.java) and [`Movie`](src/main/java/com/mycompany/blockkbusterr/entity/Movie.java).
**UI Integration:** Provides formatted methods for star display and comment preview.

### [`UserRole.java`](src/main/java/com/mycompany/blockkbusterr/entity/UserRole.java)
**Purpose:** Enum defining user privilege levels (USER, ADMIN) with display names.
**Implementation:** Simple enum with toString override for UI-friendly display.

### [`RentalStatus.java`](src/main/java/com/mycompany/blockkbusterr/entity/RentalStatus.java)
**Purpose:** Enum managing rental lifecycle states (ACTIVE, RETURNED, OVERDUE, CANCELLED).
**Implementation:** Provides state tracking for rental workflow management.

---

## Repository Classes (Data Access Layer)

### [`BaseRepository.java`](src/main/java/com/mycompany/blockkbusterr/repository/BaseRepository.java)
**Purpose:** Abstract base repository providing common CRUD operations using JPA EntityManager.
**Design Pattern:** Generic Repository Pattern with type safety for entity operations.
**Key Features:** Transactional support, common operations (save, find, delete, count), and entity lifecycle management.
**Architecture:** Eliminates code duplication across specific repository implementations.

### [`UserRepository.java`](src/main/java/com/mycompany/blockkbusterr/repository/UserRepository.java)
**Purpose:** Specialized repository for user management with authentication and search capabilities.
**Extends:** [`BaseRepository<User, Long>`](src/main/java/com/mycompany/blockkbusterr/repository/BaseRepository.java) for common operations.
**Key Methods:** Username/email lookup, authentication queries, role-based filtering, and user activation management.
**Security Integration:** Provides methods for credential validation and user status management.

### [`MovieRepository.java`](src/main/java/com/mycompany/blockkbusterr/repository/MovieRepository.java)
**Purpose:** Comprehensive movie data access with advanced filtering, search, and inventory management.
**Extends:** [`BaseRepository<Movie, Long>`](src/main/java/com/mycompany/blockkbusterr/repository/BaseRepository.java) with specialized movie queries.
**Advanced Features:** Full-text search, availability tracking, stock management, popularity rankings, and genre categorization.
**Business Intelligence:** Provides methods for analytics like most popular movies and stock level monitoring.

### [`RentalRepository.java`](src/main/java/com/mycompany/blockkbusterr/repository/RentalRepository.java)
**Purpose:** Manages rental transactions with complex querying for business analytics and overdue tracking.
**Extends:** [`BaseRepository<Rental, Long>`](src/main/java/com/mycompany/blockkbusterr/repository/BaseRepository.java) with rental-specific operations.
**Complex Queries:** Date range filtering, overdue detection, active rental tracking, and user rental statistics.
**Business Logic Integration:** Supports rental workflow management and reporting requirements.

### [`ReviewRepository.java`](src/main/java/com/mycompany/blockkbusterr/repository/ReviewRepository.java)
**Purpose:** Handles review and rating data with aggregation functions for movie ratings and user review management.
**Extends:** [`BaseRepository<Review, Long>`](src/main/java/com/mycompany/blockkbusterr/repository/BaseRepository.java) with review-specific queries.
**Aggregation Functions:** Average rating calculations, rating distributions, and review statistics.
**Data Integrity:** Prevents duplicate reviews and provides soft delete functionality.

---

## Service Classes (Business Logic Layer)

### [`DatabaseInitializationService.java`](src/main/java/com/mycompany/blockkbusterr/service/DatabaseInitializationService.java)
**Purpose:** Application startup service that initializes database with default admin user and sample movie data.
**Design Pattern:** Observer Pattern using CDI events to trigger on application startup.
**Key Features:** Conditional initialization (only runs if database is empty), transaction management for data seeding.
**Bootstrap Logic:** Creates admin user and populates sample movies for demo purposes.

### [`UserService.java`](src/main/java/com/mycompany/blockkbusterr/service/UserService.java)
**Purpose:** Comprehensive user management service handling registration, authentication, profile management, and role administration.
**Dependencies:** Injects [`UserRepository`](src/main/java/com/mycompany/blockkbusterr/repository/UserRepository.java) and [`PasswordUtil`](src/main/java/com/mycompany/blockkbusterr/util/PasswordUtil.java) for data access and password validation.
**Security Features:** Password hashing, credential validation, role management, and account activation.
**Business Rules:** Username/email uniqueness validation, password strength enforcement, and user statistics generation.

### [`MovieService.java`](src/main/java/com/mycompany/blockkbusterr/service/MovieService.java)
**Purpose:** Movie catalog management with inventory control, search capabilities, and rating integration.
**Dependencies:** Injects [`MovieRepository`](src/main/java/com/mycompany/blockkbusterr/repository/MovieRepository.java) and [`ReviewRepository`](src/main/java/com/mycompany/blockkbusterr/repository/ReviewRepository.java) for comprehensive movie data.
**Inventory Management:** Stock level monitoring, availability checking, and quantity updates with business rule validation.
**Advanced Features:** Provides movie statistics, rating aggregation, and popularity tracking through inner classes.

### [`RentalService.java`](src/main/java/com/mycompany/blockkbusterr/service/RentalService.java)
**Purpose:** Core rental business logic managing the complete rental lifecycle from creation to return.
**Complex Dependencies:** Coordinates between [`RentalRepository`](src/main/java/com/mycompany/blockkbusterr/repository/RentalRepository.java), [`UserRepository`](src/main/java/com/mycompany/blockkbusterr/repository/UserRepository.java), and [`MovieRepository`](src/main/java/com/mycompany/blockkbusterr/repository/MovieRepository.java).
**Business Rules:** Availability validation, rental period management, overdue processing, and inventory updates.
**Workflow Management:** Handles rental creation, extension, return, and cancellation with proper state transitions.

### [`ReviewService.java`](src/main/java/com/mycompany/blockkbusterr/service/ReviewService.java)
**Purpose:** Review and rating system management with validation, aggregation, and business rule enforcement.
**Data Integrity:** Prevents duplicate reviews, validates rating ranges, and ensures users can only review movies they've rented.
**Analytics:** Provides rating distributions, review statistics, and movie review summaries for business intelligence.
**User Experience:** Includes methods for checking review eligibility and managing review lifecycle.

---

## Bean Classes (Presentation Layer/JSF Controllers)

### [`SessionBean.java`](src/main/java/com/mycompany/blockkbusterr/bean/SessionBean.java)
**Purpose:** Session-scoped bean managing user authentication state and security context throughout the application.
**Scope:** `@SessionScoped` - maintains user state across multiple requests and page navigation.
**Security Integration:** Provides authentication checks, role-based access control, and session management.
**Navigation Control:** Handles login/logout workflows and redirects based on user roles.

### [`AuthenticationBean.java`](src/main/java/com/mycompany/blockkbusterr/bean/AuthenticationBean.java)
**Purpose:** Request-scoped bean handling login form processing with validation and user feedback.
**Dependencies:** Integrates with [`UserService`](src/main/java/com/mycompany/blockkbusterr/service/UserService.java) for authentication and [`SessionBean`](src/main/java/com/mycompany/blockkbusterr/bean/SessionBean.java) for session management.
**User Experience:** Provides form validation, error messaging, and login state management.
**Security Features:** Prevents multiple login attempts and handles authentication failures gracefully.

### [`UserRegistrationBean.java`](src/main/java/com/mycompany/blockkbusterr/bean/UserRegistrationBean.java)
**Purpose:** Handles new user registration with comprehensive validation, availability checks, and real-time feedback.
**Complex Validation:** Ajax-enabled username/email availability checking, password confirmation, and form validation.
**User Experience:** Real-time validation feedback, form state management, and registration workflow guidance.
**Integration:** Coordinates with [`UserService`](src/main/java/com/mycompany/blockkbusterr/service/UserService.java) for user creation and validation.

### [`MovieListBean.java`](src/main/java/com/mycompany/blockkbusterr/bean/MovieListBean.java)
**Purpose:** View-scoped bean managing movie catalog display with filtering, searching, and pagination capabilities.
**Advanced Features:** Genre filtering, availability filtering, search functionality, and dynamic content loading.
**Performance:** Efficient data loading with lazy loading patterns and filtered result sets.
**User Interface:** Provides methods for dynamic styling based on availability and movie status.

### [`MovieDetailsBean.java`](src/main/java/com/mycompany/blockkbusterr/bean/MovieDetailsBean.java)
**Purpose:** Detailed movie view controller handling movie information display, rental creation, and review submission.
**Multi-Service Integration:** Coordinates [`MovieService`](src/main/java/com/mycompany/blockkbusterr/service/MovieService.java), [`RentalService`](src/main/java/com/mycompany/blockkbusterr/service/RentalService.java), and [`ReviewService`](src/main/java/com/mycompany/blockkbusterr/service/ReviewService.java).
**Business Logic:** Handles rental eligibility checking, review submission, and rating display formatting.
**User Experience:** Provides contextual actions based on user's rental history and review status.

### [`RentalBean.java`](src/main/java/com/mycompany/blockkbusterr/bean/RentalBean.java)
**Purpose:** Comprehensive rental management bean handling rental creation, tracking, returns, and history display.
**Complex Workflows:** Manages rental lifecycle, date calculations, status updates, and inventory coordination.
**Multi-View Support:** Supports both user rental history and admin rental management interfaces.
**Business Intelligence:** Provides rental statistics, overdue tracking, and rental period calculations.

### [`ProfileBean.java`](src/main/java/com/mycompany/blockkbusterr/bean/ProfileBean.java)
**Purpose:** User profile management with edit capabilities, validation, and account statistics display.
**State Management:** Handles edit mode toggling, form validation, and profile update workflows.
**User Experience:** Provides profile statistics like active rentals, member duration, and rental history.
**Security:** Ensures users can only edit their own profiles with proper validation.

### [`AdminBean.java`](src/main/java/com/mycompany/blockkbusterr/bean/AdminBean.java)
**Purpose:** Administrator dashboard providing system-wide management capabilities and business analytics.
**Comprehensive Management:** User management, movie management, rental oversight, and system statistics.
**Business Intelligence:** Dashboard with key metrics, overdue tracking, low stock alerts, and user activity monitoring.
**Bulk Operations:** Supports bulk user management, movie quantity updates, and rental processing.

### [`MovieManagementBean.java`](src/main/java/com/mycompany/blockkbusterr/bean/MovieManagementBean.java)
**Purpose:** Dedicated bean for movie catalog management supporting both add and edit operations.
**CRUD Operations:** Complete movie lifecycle management with validation and form handling.
**Admin Features:** Movie creation, editing, validation, and form state management.
**Integration:** Works with [`MovieService`](src/main/java/com/mycompany/blockkbusterr/service/MovieService.java) for business logic and validation.

### [`DatabaseStartupBean.java`](src/main/java/com/mycompany/blockkbusterr/bean/DatabaseStartupBean.java)
**Purpose:** Singleton bean providing administrative control over database initialization for development and testing.
**Lifecycle Management:** Application-scoped singleton for database state management.
**Development Support:** Allows manual database reinitialization for testing purposes.
**Integration:** Coordinates with [`DatabaseInitializationService`](src/main/java/com/mycompany/blockkbusterr/service/DatabaseInitializationService.java) for data seeding.

---

## Utility Classes

### [`PasswordUtil.java`](src/main/java/com/mycompany/blockkbusterr/util/PasswordUtil.java)
**Purpose:** Utility class providing password security operations including hashing and validation.
**Security Implementation:** Uses secure hashing algorithms for password storage and validation.
**Validation Rules:** Enforces password strength requirements for user registration and password changes.
**Stateless Design:** Pure utility class with static methods for password operations.

---

## Configuration Classes

### [`JakartaRestConfiguration.java`](src/main/java/com/mycompany/blockkbusterr/JakartaRestConfiguration.java)
**Purpose:** Jakarta EE REST configuration class setting up REST API endpoints.
**Path Configuration:** Defines base path for REST services as "resources".
**Framework Integration:** Integrates Jakarta EE REST capabilities with the application.

---

## Application Flow

### User Registration & Authentication Flow
1. [`UserRegistrationBean`](src/main/java/com/mycompany/blockkbusterr/bean/UserRegistrationBean.java) handles form submission
2. [`UserService`](src/main/java/com/mycompany/blockkbusterr/service/UserService.java) validates and creates user via [`UserRepository`](src/main/java/com/mycompany/blockkbusterr/repository/UserRepository.java)
3. [`AuthenticationBean`](src/main/java/com/mycompany/blockkbusterr/bean/AuthenticationBean.java) processes login
4. [`SessionBean`](src/main/java/com/mycompany/blockkbusterr/bean/SessionBean.java) maintains session state

### Movie Rental Flow
1. [`MovieListBean`](src/main/java/com/mycompany/blockkbusterr/bean/MovieListBean.java) displays available movies
2. [`MovieDetailsBean`](src/main/java/com/mycompany/blockkbusterr/bean/MovieDetailsBean.java) handles rental creation
3. [`RentalService`](src/main/java/com/mycompany/blockkbusterr/service/RentalService.java) validates and processes rental
4. [`RentalBean`](src/main/java/com/mycompany/blockkbusterr/bean/RentalBean.java) manages rental lifecycle

### Review System Flow
1. [`MovieDetailsBean`](src/main/java/com/mycompany/blockkbusterr/bean/MovieDetailsBean.java) provides review interface
2. [`ReviewService`](src/main/java/com/mycompany/blockkbusterr/service/ReviewService.java) validates and processes reviews
3. Rating aggregation updates movie ratings automatically

## Key Design Decisions

**Layered Architecture:** Clear separation between presentation (Beans), business logic (Services), and data access (Repositories).

**Dependency Injection:** Extensive use of CDI for loose coupling and testability.

**Transaction Management:** Service layer handles transaction boundaries with proper rollback support.

**Security:** Role-based access control with session management and password security.

**Validation:** Multi-layer validation (Bean Validation, service-layer business rules, and presentation-layer validation).

---

## Code Cleanup Summary

**Date:** August 29, 2025

This application underwent a comprehensive code cleanup process to remove unused components while preserving all functionality. The following components were successfully removed:

### Removed Components (~1,400+ lines):

**Web Services Layer (Completely Removed):**
- Entire `webservice` package with SOAP endpoints
- `UserManagementWebService.java` - SOAP API for user management
- `MovieManagementWebService.java` - SOAP API for movie catalog management

**Data Transfer Objects (Completely Removed):**
- Entire `dto` package with all DTO classes
- `UserRequest.java` / `UserResponse.java` - User web service DTOs
- `MovieRequest.java` / `MovieResponse.java` - Movie web service DTOs
- `RentalDTO.java` / `ReviewDTO.java` - Additional unused DTOs
- `LoginRequest.java` - Authentication DTO

**Test/Demo Files:**
- `test.xhtml` - Unused test page
- `simple-login.xhtml` - Unused simple login page
- `JakartaEE10Resource.java` - Unused Jakarta EE resource class
- Empty `resources` directory

### Analysis Results:

**✅ Preserved Components:** All components actually used by the JSF application were correctly identified and preserved, including:
- Admin dashboard statistics functionality (`AdminBean` integration with service layer statistics)
- All entity classes, repositories, services, and presentation beans
- All working UI pages and functionality

**✅ Application Status:**
- Compilation successful with zero errors
- All functionality preserved and tested
- Codebase significantly cleaner and more maintainable
- Removed over 1,400 lines of genuinely unused code

This cleanup demonstrates that the original SOAP web services and DTO layers were completely unused by the JSF frontend application, making them safe for removal while maintaining all working features.

### GlassFish Migration to Apache TomEE:

**Additional Cleanup - GlassFish Components Removed:**
- `glassfish-web.xml` - GlassFish-specific configuration file
- `setup-glassfish-datasource.bat` - GlassFish setup script
- `setup-glassfish-datasource-fixed.bat` - GlassFish setup script (fixed version)
- `glassfish-admin-console-setup-guide.md` - GlassFish admin documentation
- `command-line-setup-instructions.md` - GlassFish command-line setup guide

**Migration Benefits:**
- Simplified deployment configuration focused on Apache TomEE
- Removed server-specific configuration files
- Cleaner project structure without dual-server support
- Maintained all functionality while streamlining server dependencies