# BlockKBusterr - DVD Rental Management System

![Java](https://img.shields.io/badge/Java-11-orange)
![Jakarta EE](https://img.shields.io/badge/Jakarta%20EE-10.0-blue)
![JSF](https://img.shields.io/badge/JSF-4.0-green)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![Apache TomEE](https://img.shields.io/badge/Apache%20TomEE-9.1.2-red)
![Maven](https://img.shields.io/badge/Maven-3.6+-purple)

A comprehensive DVD rental management system built with Jakarta EE, featuring user management, movie catalog, rental tracking, and review system with automatic database initialization.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [System Requirements](#system-requirements)
- [Quick Start](#quick-start)
- [Installation & Setup](#installation--setup)
- [Database Configuration](#database-configuration)
- [Usage Guide](#usage-guide)
- [Technical Architecture](#technical-architecture)
- [Development Setup](#development-setup)
- [API Reference](#api-reference)
- [Deployment](#deployment)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [Documentation](#documentation)

## 🎯 Overview

BlockKBusterr is a modern DVD rental management system designed for rental stores and customers. Built using Jakarta EE 10 with JSF for the frontend, it provides a complete solution for managing movie inventory, user accounts, rentals, and reviews.

### Key Highlights

- **Modern Technology Stack**: Jakarta EE 10, JSF 4.0, JPA with OpenJPA
- **Automatic Setup**: Database initialization with sample data and admin user
- **Comprehensive Features**: Complete rental workflow from browsing to return
- **Security**: Role-based access control with BCrypt password hashing
- **Responsive Design**: User-friendly web interface with CSS styling
- **Production Ready**: Configured for Apache TomEE deployment

## ✨ Features

### 🔐 User Management
- **User Registration & Authentication**: Secure account creation with email validation
- **Role-Based Access Control**: Separate interfaces for customers and administrators
- **Profile Management**: Users can update personal information and view rental history
- **Password Security**: BCrypt hashing with configurable strength

### 🎬 Movie Catalog
- **Comprehensive Movie Database**: Title, genre, release year, duration, description
- **Inventory Management**: Stock tracking with availability status
- **Advanced Search & Filtering**: Search by title, filter by genre and availability
- **Movie Details**: Detailed view with ratings, reviews, and rental options

### 📅 Rental System
- **Complete Rental Workflow**: Browse → Rent → Track → Return
- **Rental Tracking**: Active rentals, due dates, and overdue management
- **Rental History**: Complete history for users and detailed reporting for admins
- **Business Rules**: Availability checking, rental period management

### ⭐ Review & Rating System
- **User Reviews**: Customers can rate and review movies they've rented
- **Rating Aggregation**: Automatic calculation of average ratings
- **Review Management**: Display formatted reviews with star ratings
- **Business Logic**: Prevents duplicate reviews, validates rating ranges

### 👥 Administration
- **Admin Dashboard**: System statistics, user management, inventory control
- **User Management**: View all users, manage accounts, track activity
- **Movie Management**: Add/edit movies, manage inventory, monitor popularity
- **Rental Oversight**: Monitor all rentals, handle overdue items, generate reports

### 🚀 Automatic Database Initialization
- **Sample Data**: 15 popular movies across various genres
- **Default Admin User**: Ready-to-use admin account (`admin`/`admin123`)
- **Smart Setup**: Only initializes on empty database, prevents duplicates
- **Configurable**: Can be disabled or customized for production

## 💻 System Requirements

### Prerequisites
- **Java Development Kit (JDK) 11** or higher
- **Apache Maven 3.6+** for build management
- **MySQL 8.0+** database server
- **Git** for version control

### Recommended Environment
- **Operating System**: Windows 10/11, macOS 10.15+, or Linux
- **RAM**: 4GB minimum, 8GB recommended
- **Storage**: 2GB free space for application and database
- **Network**: Internet connection for Maven dependencies

## 🚀 Quick Start

### Option 1: Automatic Setup (Recommended)

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd DVD-Project
   ```

2. **Setup MySQL database**:
   ```sql
   CREATE DATABASE blockkbusterr CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. **Configure database connection**:
   Edit [`src/main/webapp/WEB-INF/tomee.xml`](src/main/webapp/WEB-INF/tomee.xml):
   ```xml
   <Resource id="blockkbusterrDS" type="DataSource">
       JdbcUrl jdbc:mysql://localhost:3306/blockkbusterr?useSSL=false&amp;allowPublicKeyRetrieval=true&amp;serverTimezone=UTC
       UserName root
       Password YOUR_PASSWORD
   </Resource>
   ```

4. **Run the application**:
   ```bash
   ./run-with-tomee.bat    # Windows
   # or
   mvn -f pom-tomee.xml clean package tomee:run    # Any OS
   ```

5. **Access the application**:
   - **URL**: http://localhost:8080/blockkbusterr/
   - **Admin Login**: `admin` / `admin123`
   - **Database**: Automatically initialized with sample movies

### Option 2: Manual Database Setup

If you prefer to set up the database manually, see the [Database Configuration](#database-configuration) section below.

## 🔧 Installation & Setup

### Step 1: Environment Setup

1. **Install Java 11+**:
   ```bash
   # Verify Java installation
   java -version
   javac -version
   ```

2. **Install Maven**:
   ```bash
   # Verify Maven installation
   mvn -version
   ```

3. **Install MySQL**:
   - Download and install MySQL 8.0+
   - Start MySQL service
   - Create database user if needed

### Step 2: Project Setup

1. **Clone and navigate**:
   ```bash
   git clone <repository-url>
   cd DVD-Project
   ```

2. **Verify project structure**:
   ```
   DVD-Project/
   ├── src/main/
   │   ├── java/com/mycompany/blockkbusterr/
   │   ├── resources/
   │   └── webapp/
   ├── pom.xml
   ├── pom-tomee.xml
   ├── run-with-tomee.bat
   └── README.md
   ```

### Step 3: Database Configuration

1. **Create database**:
   ```sql
   mysql -u root -p
   CREATE DATABASE blockkbusterr CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   GRANT ALL PRIVILEGES ON blockkbusterr.* TO 'root'@'localhost';
   FLUSH PRIVILEGES;
   ```

2. **Configure connection**:
   Update database credentials in [`src/main/webapp/WEB-INF/tomee.xml`](src/main/webapp/WEB-INF/tomee.xml):
   ```xml
   <Resource id="blockkbusterrDS" type="DataSource">
       JdbcUrl jdbc:mysql://localhost:3306/blockkbusterr?useSSL=false&amp;allowPublicKeyRetrieval=true&amp;serverTimezone=UTC
       UserName YOUR_USERNAME
       Password YOUR_PASSWORD
   </Resource>
   ```

### Step 4: Build and Run

1. **Build the project**:
   ```bash
   mvn clean compile
   ```

2. **Run with embedded TomEE**:
   ```bash
   mvn -f pom-tomee.xml clean package tomee:run
   ```

3. **Access application**:
   - Open browser to: http://localhost:8080/blockkbusterr/
   - Login with: `admin` / `admin123`

## 🗄️ Database Configuration

### Automatic Initialization (Default)

The application includes an automatic database initialization system that:

- **Creates default admin user**: `admin` / `admin123`
- **Populates sample movies**: 15 popular movies across various genres
- **Sets up proper relationships**: Users, movies, rentals, reviews
- **Runs only once**: Smart detection prevents duplicate data

#### Configuration Options

**System Properties**:
```bash
# Disable initialization
-Dblockkbusterr.db.initialize=false

# Custom admin credentials
-Dblockkbusterr.admin.username=myadmin
-Dblockkbusterr.admin.password=mypassword123
```

**Configuration File** ([`src/main/resources/database-init.properties`](src/main/resources/database-init.properties)):
```properties
# Enable/disable initialization
blockkbusterr.db.initialize=true

# Admin credentials
blockkbusterr.admin.username=admin
blockkbusterr.admin.password=admin123
```

### Manual Database Setup

If you prefer manual setup or need custom data:

1. **Disable automatic initialization**:
   ```bash
   -Dblockkbusterr.db.initialize=false
   ```

2. **Use provided SQL script**:
   ```bash
   mysql -u root -p blockkbusterr < database-initialization.sql
   ```

3. **Or create custom data** following the entity models in [`src/main/java/com/mycompany/blockkbusterr/entity/`](src/main/java/com/mycompany/blockkbusterr/entity/)

### Default Sample Data

#### Admin User
- **Username**: `admin`
- **Password**: `admin123`
- **Email**: `admin@blockkbusterr.com`
- **Role**: Administrator

#### Sample Movies (15 movies across genres)
- **Action**: The Dark Knight, Gladiator
- **Crime**: The Godfather, Pulp Fiction, Goodfellas, The Departed
- **Drama**: The Shawshank Redemption, Forrest Gump
- **Sci-Fi**: Inception, The Matrix, Star Wars: A New Hope, Interstellar
- **Fantasy**: The Lord of the Rings: Fellowship of the Ring
- **Thriller**: The Silence of the Lambs
- **War**: Saving Private Ryan

Each movie includes realistic release dates, durations, descriptions, and inventory quantities (2-4 copies each).

## 📖 Usage Guide

### For End Users (Customers)

#### Getting Started
1. **Register Account**: Click "Register" and create your account
2. **Browse Movies**: View available movies on the main page
3. **Search & Filter**: Use search box and genre filters to find movies
4. **View Details**: Click on any movie to see details, ratings, and reviews

#### Renting Movies
1. **Check Availability**: Green badge indicates available copies
2. **Rent Movie**: Click "Rent this Movie" on movie details page
3. **Track Rentals**: View active rentals in your profile
4. **Return Movies**: Use "Return Movie" button when finished

#### Managing Your Account
1. **Profile**: Access via user menu to update personal information
2. **Rental History**: View all past rentals and their status
3. **Reviews**: Rate and review movies you've rented
4. **Account Settings**: Update password and contact information

### For Administrators

#### Admin Dashboard
- **Access**: Login with admin credentials to see admin menu
- **Statistics**: View system overview, active rentals, user counts
- **Quick Actions**: Manage overdue rentals, low stock alerts

#### User Management
1. **View All Users**: Admin → User Management
2. **User Details**: Click on users to see rental history and account status
3. **Account Control**: Activate/deactivate user accounts as needed

#### Movie Management
1. **Add Movies**: Admin → Add Movie with complete details
2. **Edit Movies**: Update movie information, adjust inventory
3. **Inventory Control**: Monitor stock levels and availability
4. **Popular Movies**: Track rental statistics and user preferences

#### Rental Management
1. **All Rentals**: View system-wide rental activity
2. **Overdue Tracking**: Monitor and manage overdue rentals
3. **Rental Reports**: Generate reports for business analysis
4. **Return Processing**: Process returns and update inventory

### Common Workflows

#### Customer Rental Workflow
```
Browse Movies → Search/Filter → Movie Details → Rent → Track → Return → Review
```

#### Admin Movie Management
```
Add Movie → Set Inventory → Monitor Rentals → Adjust Stock → Generate Reports
```

## 🏗️ Technical Architecture

### Architecture Overview

BlockKBusterr follows a layered n-tier architecture with clear separation of concerns:

```
┌─────────────────────────────────────────┐
│          Presentation Layer             │
│    (JSF Beans - *.java in bean/)       │
├─────────────────────────────────────────┤
│           Service Layer                 │
│   (Business Logic - *.java in service/) │
├─────────────────────────────────────────┤
│          Repository Layer               │
│  (Data Access - *.java in repository/)  │
├─────────────────────────────────────────┤
│            Entity Layer                 │
│   (Data Models - *.java in entity/)     │
└─────────────────────────────────────────┘
```

### Design Patterns

- **Repository Pattern**: Data access abstraction with [`BaseRepository`](src/main/java/com/mycompany/blockkbusterr/repository/BaseRepository.java)
- **Service Layer Pattern**: Business logic encapsulation
- **Managed Bean Pattern**: JSF integration with CDI
- **Dependency Injection**: CDI throughout the application
- **Observer Pattern**: Database initialization on startup

### Key Components

#### Entity Layer
- **[`User.java`](src/main/java/com/mycompany/blockkbusterr/entity/User.java)**: User accounts with authentication and roles
- **[`Movie.java`](src/main/java/com/mycompany/blockkbusterr/entity/Movie.java)**: Movie catalog with inventory and ratings
- **[`Rental.java`](src/main/java/com/mycompany/blockkbusterr/entity/Rental.java)**: Rental transactions with status tracking
- **[`Review.java`](src/main/java/com/mycompany/blockkbusterr/entity/Review.java)**: User reviews and ratings system

#### Repository Layer
- **[`BaseRepository.java`](src/main/java/com/mycompany/blockkbusterr/repository/BaseRepository.java)**: Generic repository with common CRUD operations
- **[`UserRepository.java`](src/main/java/com/mycompany/blockkbusterr/repository/UserRepository.java)**: User management with authentication queries
- **[`MovieRepository.java`](src/main/java/com/mycompany/blockkbusterr/repository/MovieRepository.java)**: Movie catalog with search and filtering
- **[`RentalRepository.java`](src/main/java/com/mycompany/blockkbusterr/repository/RentalRepository.java)**: Rental tracking with analytics
- **[`ReviewRepository.java`](src/main/java/com/mycompany/blockkbusterr/repository/ReviewRepository.java)**: Review management with aggregation

#### Service Layer
- **[`UserService.java`](src/main/java/com/mycompany/blockkbusterr/service/UserService.java)**: User registration, authentication, profile management
- **[`MovieService.java`](src/main/java/com/mycompany/blockkbusterr/service/MovieService.java)**: Movie catalog management with inventory control
- **[`RentalService.java`](src/main/java/com/mycompany/blockkbusterr/service/RentalService.java)**: Rental workflow with business rules
- **[`ReviewService.java`](src/main/java/com/mycompany/blockkbusterr/service/ReviewService.java)**: Review system with validation
- **[`DatabaseInitializationService.java`](src/main/java/com/mycompany/blockkbusterr/service/DatabaseInitializationService.java)**: Automatic database setup

#### Presentation Layer (JSF Beans)
- **[`SessionBean.java`](src/main/java/com/mycompany/blockkbusterr/bean/SessionBean.java)**: Session management and authentication state
- **[`AuthenticationBean.java`](src/main/java/com/mycompany/blockkbusterr/bean/AuthenticationBean.java)**: Login form processing
- **[`UserRegistrationBean.java`](src/main/java/com/mycompany/blockkbusterr/bean/UserRegistrationBean.java)**: User registration with validation
- **[`MovieListBean.java`](src/main/java/com/mycompany/blockkbusterr/bean/MovieListBean.java)**: Movie catalog display with filtering
- **[`MovieDetailsBean.java`](src/main/java/com/mycompany/blockkbusterr/bean/MovieDetailsBean.java)**: Movie details and rental actions
- **[`RentalBean.java`](src/main/java/com/mycompany/blockkbusterr/bean/RentalBean.java)**: Rental management and history
- **[`AdminBean.java`](src/main/java/com/mycompany/blockkbusterr/bean/AdminBean.java)**: Administrator dashboard and management

### Technology Stack Details

#### Core Technologies
- **Jakarta EE 10**: Enterprise Java platform
- **JSF 4.0**: Component-based web framework
- **JPA with OpenJPA**: Object-relational mapping
- **CDI**: Contexts and Dependency Injection
- **Bean Validation**: Input validation framework

#### Database & Persistence
- **MySQL 8.0+**: Primary database
- **OpenJPA**: JPA implementation with MySQL dialect
- **Connection Pooling**: Configured in TomEE with validation
- **Transaction Management**: JTA with automatic rollback

#### Security
- **BCrypt**: Password hashing with configurable rounds
- **Role-Based Access**: USER and ADMIN roles
- **Session Management**: JSF session-scoped beans
- **Input Validation**: Multi-layer validation (Bean Validation + business rules)

#### Build & Deployment
- **Maven**: Build automation and dependency management
- **Apache TomEE 9.1.2**: Jakarta EE application server
- **WAR Packaging**: Standard web application archive

## 👨‍💻 Development Setup

### Development Environment

1. **IDE Setup** (Recommended: IntelliJ IDEA or Eclipse):
   ```bash
   # Import as Maven project
   # Configure JDK 11+
   # Install Jakarta EE/JSF plugins
   ```

2. **Database Development Setup**:
   ```bash
   # Use development database
   CREATE DATABASE blockkbusterr_dev;
   # Update tomee.xml with dev credentials
   ```

3. **Hot Reload Configuration**:
   The TomEE Maven plugin is configured for development with hot reload:
   ```xml
   <reloadOnUpdate>true</reloadOnUpdate>
   <synchronization>
       <extensions>
           <extension>.class</extension>
           <extension>.xhtml</extension>
           <extension>.css</extension>
       </extensions>
   </synchronization>
   ```

### Build Commands

```bash
# Clean build
mvn clean compile

# Run tests (when available)
mvn test

# Package WAR file
mvn package

# Run with embedded TomEE (development)
mvn -f pom-tomee.xml clean package tomee:run

# Deploy to external TomEE
mvn package
# Copy target/BlockkBusterr-1.0-SNAPSHOT.war to TomEE webapps/
```

### Development Workflow

1. **Code Changes**: Edit Java/XHTML/CSS files
2. **Hot Reload**: Changes automatically detected (for configured file types)
3. **Manual Restart**: Required for configuration changes
4. **Database Reset**: Stop server, clear database, restart for fresh data

### Project Structure

```
src/main/
├── java/com/mycompany/blockkbusterr/
│   ├── bean/              # JSF Managed Beans
│   ├── entity/            # JPA Entities
│   ├── repository/        # Data Access Layer
│   ├── service/           # Business Logic Layer
│   ├── util/              # Utility Classes
│   └── JakartaRestConfiguration.java
├── resources/
│   ├── META-INF/
│   │   ├── persistence.xml    # JPA Configuration
│   │   └── resources.xml      # Resource Configuration
│   └── database-init.properties  # DB Init Settings
└── webapp/
    ├── WEB-INF/
    │   ├── web.xml           # Web Application Config
    │   ├── tomee.xml         # TomEE DataSource Config
    │   └── beans.xml         # CDI Configuration
    ├── resources/
    │   ├── css/style.css     # Application Styles
    │   └── img/              # Images
    ├── templates/
    │   └── template.xhtml    # JSF Template
    └── *.xhtml              # JSF Pages
```

## 📚 API Reference

### Entity Models

#### User Entity
```java
@Entity
public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;  // BCrypt hashed
    private UserRole role;    // USER or ADMIN
    private boolean active;
    private LocalDateTime createdAt;
    // Relationships with Rental and Review
}
```

#### Movie Entity
```java
@Entity
public class Movie {
    private Long id;
    private String title;
    private LocalDate releaseDate;
    private int duration;     // minutes
    private String genre;
    private String description;
    private String imageUrl;
    private int quantity;
    private boolean active;
    private LocalDateTime createdAt;
    // Methods: getAvailableQuantity(), getAverageRating()
}
```

#### Rental Entity
```java
@Entity
public class Rental {
    private Long id;
    private User user;
    private Movie movie;
    private LocalDate rentalDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private RentalStatus status;  // ACTIVE, RETURNED, OVERDUE, CANCELLED
    private LocalDateTime createdAt;
    // Methods: isOverdue(), getDurationDays()
}
```

### Service Layer APIs

#### UserService
```java
@ApplicationScoped
public class UserService {
    // Authentication
    User authenticate(String username, String password);
    boolean validateCredentials(String username, String password);
    
    // User Management
    User createUser(User user);
    User updateUser(User user);
    User findByUsername(String username);
    List<User> findAllUsers();
    
    // Statistics
    long getTotalUserCount();
    List<User> getActiveUsers();
}
```

#### MovieService
```java
@ApplicationScoped
public class MovieService {
    // Movie Management
    Movie saveMovie(Movie movie);
    Movie findById(Long id);
    List<Movie> findAllMovies();
    List<Movie> searchMovies(String query);
    List<Movie> filterByGenre(String genre);
    
    // Inventory
    boolean isAvailable(Long movieId);
    void updateQuantity(Long movieId, int quantity);
    List<Movie> getLowStockMovies();
}
```

#### RentalService
```java
@ApplicationScoped
public class RentalService {
    // Rental Operations
    Rental createRental(Long userId, Long movieId);
    Rental returnRental(Long rentalId);
    List<Rental> getUserActiveRentals(Long userId);
    List<Rental> getOverdueRentals();
    
    // Statistics
    long getTotalActiveRentals();
    List<Movie> getMostPopularMovies();
}
```

### JSF Bean APIs

#### SessionBean (Session Management)
```java
@SessionScoped
public class SessionBean {
    // Authentication State
    User getCurrentUser();
    boolean isLoggedIn();
    boolean isAdmin();
    
    // Navigation
    String login(User user);
    String logout();
    String redirectToHome();
}
```

#### MovieListBean (Movie Catalog)
```java
@ViewScoped
public class MovieListBean {
    // Display Properties
    List<Movie> getMovies();
    List<Movie> getFilteredMovies();
    
    // Search & Filter
    void setSearchQuery(String query);
    void setSelectedGenre(String genre);
    void applyFilters();
    
    // UI Support
    String getAvailabilityClass(Movie movie);
    List<String> getGenres();
}
```

## 🚀 Deployment

### Production Deployment

#### Option 1: External TomEE Server

1. **Prepare production environment**:
   ```bash
   # Download and install TomEE 9.1.2+
   # Configure production database
   # Set environment variables
   ```

2. **Build production WAR**:
   ```bash
   mvn clean package -Pproduction
   ```

3. **Configure production settings**:
   ```bash
   # Disable database initialization
   -Dblockkbusterr.db.initialize=false
   
   # Set production database
   # Update tomee.xml with production credentials
   ```

4. **Deploy**:
   ```bash
   cp target/BlockkBusterr-1.0-SNAPSHOT.war $TOMEE_HOME/webapps/
   ```

#### Option 2: Docker Deployment

Create `Dockerfile`:
```dockerfile
FROM tomee:9.1.2-jre11-plume

COPY target/BlockkBusterr-1.0-SNAPSHOT.war $CATALINA_HOME/webapps/

EXPOSE 8080

CMD ["catalina.sh", "run"]
```

Build and run:
```bash
mvn package
docker build -t blockkbusterr .
docker run -p 8080:8080 blockkbusterr
```

#### Option 3: Cloud Deployment

**AWS Elastic Beanstalk**:
1. Create Elastic Beanstalk Java application
2. Upload WAR file
3. Configure RDS MySQL instance
4. Set environment variables

**Heroku**:
1. Use Heroku Maven plugin
2. Configure Heroku Postgres add-on
3. Set config vars for database connection

### Production Configuration

#### Security Settings
```properties
# Change default admin password
-Dblockkbusterr.admin.password=SECURE_PASSWORD

# Disable initialization
-Dblockkbusterr.db.initialize=false

# Production database
-Ddb.url=jdbc:mysql://prod-server:3306/blockkbusterr
```

#### Performance Tuning
```xml
<!-- In tomee.xml -->
<Resource id="blockkbusterrDS" type="DataSource">
    InitialSize 10
    MaxActive 50
    MaxIdle 20
    MinIdle 10
    MaxWait 5000
</Resource>
```

#### Monitoring & Logging
```properties
# Production logging
openjpa.Log=DefaultLevel=WARN,SQL=WARN

# Enable JMX monitoring
-Dcom.sun.management.jmxremote=true
```

## 🐛 Troubleshooting

### Common Issues

#### Database Connection Issues

**Problem**: `SQLException: Access denied for user`
```
Solution:
1. Verify MySQL credentials in tomee.xml
2. Check MySQL user permissions
3. Ensure MySQL service is running
```

**Problem**: `Table doesn't exist`
```
Solution:
1. Ensure database initialization ran
2. Check JPA schema generation settings
3. Verify persistence.xml configuration
```

#### Application Startup Issues

**Problem**: `CDI deployment failure`
```
Solution:
1. Check beans.xml file exists
2. Verify CDI annotations on beans
3. Check for circular dependencies
```

**Problem**: `JSF ViewExpiredException`
```
Solution:
1. Increase session timeout in web.xml
2. Check for proper ViewScoped usage
3. Verify navigation rules
```

#### Runtime Issues

**Problem**: Movies not displaying
```
Solution:
1. Check database initialization logs
2. Verify movie entity relationships
3. Check JSF rendering conditions
```

**Problem**: Login not working
```
Solution:
1. Verify password hashing configuration
2. Check authentication logic
3. Review session management
```

### Debugging Tips

#### Enable Debug Logging
```xml
<!-- In persistence.xml -->
<property name="openjpa.Log" value="DefaultLevel=TRACE,SQL=TRACE"/>
```

#### Check Application Logs
```bash
# TomEE logs location
$TOMEE_HOME/logs/catalina.out
$TOMEE_HOME/logs/localhost.log
```

#### Database Debugging
```sql
-- Check database contents
SELECT * FROM User WHERE role = 'ADMIN';
SELECT COUNT(*) FROM Movie;
SELECT * FROM Rental WHERE status = 'ACTIVE';
```

### Performance Issues

#### Slow Database Queries
1. Add database indexes for frequently queried columns
2. Optimize JPA queries using JOIN FETCH
3. Enable query result caching

#### Memory Issues
1. Increase JVM heap size: `-Xmx2g`
2. Monitor CDI bean scopes
3. Check for memory leaks in JSF backing beans

### Getting Help

1. **Check Logs**: Always start with application and database logs
2. **Review Documentation**: See detailed docs in [`BlockKBusterr_Technical_Documentation.md`](BlockKBusterr_Technical_Documentation.md)
3. **Database Issues**: Refer to [`DATABASE_INITIALIZATION_README.md`](DATABASE_INITIALIZATION_README.md)
4. **Community**: Search Jakarta EE and JSF communities for similar issues

## 🤝 Contributing

### Development Workflow

1. **Fork the repository**
2. **Create feature branch**: `git checkout -b feature/your-feature`
3. **Make changes and test thoroughly**
4. **Commit with descriptive messages**
5. **Push and create pull request**

### Coding Standards

- **Java**: Follow Oracle Java conventions
- **JSF**: Use semantic component names and proper binding
- **SQL**: Use meaningful table and column names
- **Documentation**: Update relevant documentation with changes

### Testing Guidelines

1. **Unit Tests**: Test service layer business logic
2. **Integration Tests**: Test with database using clean data
3. **Manual Testing**: Verify UI functionality across different browsers
4. **Security Testing**: Verify authentication and authorization

### Code Review Process

1. **Pull Request Template**: Include description of changes and testing done
2. **Review Checklist**: Security, performance, code quality
3. **Testing**: All tests must pass before merge
4. **Documentation**: Update relevant documentation

## 📖 Documentation

### Available Documentation

- **[`README.md`](README.md)** - This comprehensive guide (you are here)
- **[`BlockKBusterr_Technical_Documentation.md`](BlockKBusterr_Technical_Documentation.md)** - Detailed technical architecture and component documentation
- **[`DATABASE_INITIALIZATION_README.md`](DATABASE_INITIALIZATION_README.md)** - Database setup and initialization guide
- **[`IMPLEMENTATION_SUMMARY.md`](IMPLEMENTATION_SUMMARY.md)** - Summary of implemented features and components
- **[`database-initialization-plan.md`](database-initialization-plan.md)** - Technical implementation details for database initialization

### JavaDoc Documentation

Generate API documentation:
```bash
mvn javadoc:javadoc
# Open target/site/apidocs/index.html
```

### Database Schema Documentation

The application uses JPA entities with automatic schema generation. For detailed entity relationships and database structure, see:
- Entity classes in [`src/main/java/com/mycompany/blockkbusterr/entity/`](src/main/java/com/mycompany/blockkbusterr/entity/)
- Persistence configuration in [`src/main/resources/META-INF/persistence.xml`](src/main/resources/META-INF/persistence.xml)

---

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For support and questions:
- Review the troubleshooting section above
- Check existing documentation
- Create an issue in the repository

---

**Happy Coding! 🎬📀**