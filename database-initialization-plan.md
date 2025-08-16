# Database Initialization Implementation Plan

## Overview
This document contains the complete implementation for database initialization with default admin user and sample movies using Java-based startup bean approach.

## Implementation Strategy

### 1. Database Initialization Service
**File**: `src/main/java/com/mycompany/blockkbusterr/service/DatabaseInitializationService.java`

```java
package com.mycompany.blockkbusterr.service;

import com.mycompany.blockkbusterr.entity.Movie;
import com.mycompany.blockkbusterr.entity.User;
import com.mycompany.blockkbusterr.entity.UserRole;
import com.mycompany.blockkbusterr.repository.MovieRepository;
import com.mycompany.blockkbusterr.repository.UserRepository;
import com.mycompany.blockkbusterr.util.PasswordUtil;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.logging.Logger;

@ApplicationScoped
public class DatabaseInitializationService {
    
    private static final Logger logger = Logger.getLogger(DatabaseInitializationService.class.getName());
    
    @Inject
    private UserRepository userRepository;
    
    @Inject
    private MovieRepository movieRepository;
    
    /**
     * Initialize database on application startup
     */
    public void onApplicationStart(@Observes @Initialized(ApplicationScoped.class) Object init) {
        try {
            logger.info("Starting database initialization...");
            initializeDefaultData();
            logger.info("Database initialization completed successfully.");
        } catch (Exception e) {
            logger.severe("Database initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Transactional
    private void initializeDefaultData() {
        initializeAdminUser();
        initializeSampleMovies();
    }
    
    /**
     * Create default admin user if not exists
     */
    private void initializeAdminUser() {
        String adminUsername = "admin";
        
        if (userRepository.existsByUsername(adminUsername)) {
            logger.info("Admin user already exists, skipping creation.");
            return;
        }
        
        try {
            User admin = new User();
            admin.setFirstName("System");
            admin.setLastName("Administrator");
            admin.setEmail("admin@blockkbusterr.com");
            admin.setUsername(adminUsername);
            admin.setPassword(PasswordUtil.hashPassword("admin123"));
            admin.setRole(UserRole.ADMIN);
            admin.setActive(true);
            admin.setCreatedAt(LocalDateTime.now());
            
            userRepository.save(admin);
            logger.info("Default admin user created successfully: " + adminUsername);
            
        } catch (Exception e) {
            logger.severe("Failed to create admin user: " + e.getMessage());
        }
    }
    
    /**
     * Create sample movies if database is empty
     */
    private void initializeSampleMovies() {
        long movieCount = movieRepository.count();
        
        if (movieCount > 0) {
            logger.info("Movies already exist (" + movieCount + "), skipping sample data creation.");
            return;
        }
        
        logger.info("Creating sample movie dataset...");
        
        Movie[] sampleMovies = {
            createMovie("The Shawshank Redemption", "1994-09-23", 142, "Drama", 3,
                "Two imprisoned friends bond over a number of years, finding solace and eventual redemption through acts of common decency.",
                "https://example.com/shawshank.jpg"),
                
            createMovie("The Godfather", "1972-03-24", 175, "Crime", 2,
                "An organized crime dynasty's aging patriarch transfers control of his clandestine empire to his reluctant son.",
                "https://example.com/godfather.jpg"),
                
            createMovie("The Dark Knight", "2008-07-18", 152, "Action", 4,
                "When the menace known as the Joker wreaks havoc on Gotham City, Batman must face his greatest challenge.",
                "https://example.com/darkknight.jpg"),
                
            createMovie("Pulp Fiction", "1994-10-14", 154, "Crime", 2,
                "The lives of two mob hitmen, a boxer, a gangster and his wife intertwine in four tales of violence and redemption.",
                "https://example.com/pulpfiction.jpg"),
                
            createMovie("Forrest Gump", "1994-07-06", 142, "Drama", 3,
                "The presidencies of Kennedy and Johnson through the eyes of an Alabama man with an IQ of 75.",
                "https://example.com/forrestgump.jpg"),
                
            createMovie("Inception", "2010-07-16", 148, "Sci-Fi", 3,
                "A thief who steals corporate secrets through dream-sharing technology is given the inverse task of planting an idea.",
                "https://example.com/inception.jpg"),
                
            createMovie("The Matrix", "1999-03-31", 136, "Sci-Fi", 2,
                "A computer programmer discovers that reality as he knows it is a simulation and must fight to free humanity.",
                "https://example.com/matrix.jpg"),
                
            createMovie("Goodfellas", "1990-09-21", 146, "Crime", 2,
                "The story of Henry Hill and his life in the mob, covering his relationship with his wife and partners.",
                "https://example.com/goodfellas.jpg"),
                
            createMovie("The Lord of the Rings: The Fellowship of the Ring", "2001-12-19", 178, "Fantasy", 2,
                "A meek Hobbit and eight companions set out on a journey to destroy the powerful One Ring.",
                "https://example.com/lotr1.jpg"),
                
            createMovie("Star Wars: Episode IV - A New Hope", "1977-05-25", 121, "Sci-Fi", 3,
                "Luke Skywalker joins forces with a Jedi Knight to rescue Princess Leia and save the galaxy.",
                "https://example.com/starwars.jpg"),
                
            createMovie("The Silence of the Lambs", "1991-02-14", 118, "Thriller", 2,
                "A young FBI cadet must receive help from Dr. Hannibal Lecter to catch another serial killer.",
                "https://example.com/silencelambs.jpg"),
                
            createMovie("Saving Private Ryan", "1998-07-24", 169, "War", 2,
                "Following the Normandy Landings, a group of soldiers go behind enemy lines to retrieve a paratrooper.",
                "https://example.com/privatyan.jpg"),
                
            createMovie("Interstellar", "2014-11-07", 169, "Sci-Fi", 3,
                "A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival.",
                "https://example.com/interstellar.jpg"),
                
            createMovie("The Departed", "2006-10-06", 151, "Crime", 2,
                "An undercover cop and a police informant play a cat-and-mouse game in the Boston underworld.",
                "https://example.com/departed.jpg"),
                
            createMovie("Gladiator", "2000-05-05", 155, "Action", 2,
                "A former Roman General seeks vengeance against the corrupt emperor who murdered his family.",
                "https://example.com/gladiator.jpg")
        };
        
        int successCount = 0;
        for (Movie movie : sampleMovies) {
            try {
                movieRepository.save(movie);
                successCount++;
                logger.info("Created movie: " + movie.getTitle());
            } catch (Exception e) {
                logger.warning("Failed to create movie '" + movie.getTitle() + "': " + e.getMessage());
            }
        }
        
        logger.info("Sample movie creation completed. Successfully created " + successCount + " out of " + sampleMovies.length + " movies.");
    }
    
    /**
     * Helper method to create movie objects
     */
    private Movie createMovie(String title, String releaseDateStr, int duration, String genre, int quantity, String description, String imageUrl) {
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setReleaseDate(LocalDate.parse(releaseDateStr));
        movie.setDuration(duration);
        movie.setGenre(genre);
        movie.setQuantity(quantity);
        movie.setDescription(description);
        movie.setImageUrl(imageUrl);
        movie.setActive(true);
        movie.setCreatedAt(LocalDateTime.now());
        return movie;
    }
}
```

### 2. Alternative Startup Bean Approach
**File**: `src/main/java/com/mycompany/blockkbusterr/bean/DatabaseStartupBean.java`

```java
package com.mycompany.blockkbusterr.bean;

import com.mycompany.blockkbusterr.service.DatabaseInitializationService;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import java.util.logging.Logger;

@Singleton
@Startup
public class DatabaseStartupBean {
    
    private static final Logger logger = Logger.getLogger(DatabaseStartupBean.class.getName());
    
    @Inject
    private DatabaseInitializationService initService;
    
    @PostConstruct
    public void initialize() {
        logger.info("DatabaseStartupBean initializing...");
        try {
            initService.initializeDatabase();
        } catch (Exception e) {
            logger.severe("Database startup initialization failed: " + e.getMessage());
        }
    }
}
```

### 3. Enhanced Initialization Service with Configuration
**Enhanced version with configuration support**:

```java
// Add to DatabaseInitializationService
private static final String INIT_PROPERTY = "blockkbusterr.db.initialize";
private static final String ADMIN_USERNAME_PROPERTY = "blockkbusterr.admin.username";
private static final String ADMIN_PASSWORD_PROPERTY = "blockkbusterr.admin.password";

private boolean shouldInitialize() {
    String initFlag = System.getProperty(INIT_PROPERTY, "true");
    return Boolean.parseBoolean(initFlag);
}

private String getAdminUsername() {
    return System.getProperty(ADMIN_USERNAME_PROPERTY, "admin");
}

private String getAdminPassword() {
    return System.getProperty(ADMIN_PASSWORD_PROPERTY, "admin123");
}
```

## Configuration Options

### System Properties
- `blockkbusterr.db.initialize=true/false` - Enable/disable initialization
- `blockkbusterr.admin.username=admin` - Set admin username
- `blockkbusterr.admin.password=admin123` - Set admin password

### TomEE Configuration
Add to `conf/system.properties`:
```properties
blockkbusterr.db.initialize=true
blockkbusterr.admin.username=admin
blockkbusterr.admin.password=admin123
```

## Default Data Created

### Admin User
- **Username**: `admin`
- **Password**: `admin123` (BCrypt hashed)
- **Email**: `admin@blockkbusterr.com`
- **Name**: System Administrator
- **Role**: ADMIN
- **Status**: Active

### Sample Movies (15 movies)
1. The Shawshank Redemption (1994) - Drama - 3 copies
2. The Godfather (1972) - Crime - 2 copies
3. The Dark Knight (2008) - Action - 4 copies
4. Pulp Fiction (1994) - Crime - 2 copies
5. Forrest Gump (1994) - Drama - 3 copies
6. Inception (2010) - Sci-Fi - 3 copies
7. The Matrix (1999) - Sci-Fi - 2 copies
8. Goodfellas (1990) - Crime - 2 copies
9. LOTR: Fellowship of the Ring (2001) - Fantasy - 2 copies
10. Star Wars: A New Hope (1977) - Sci-Fi - 3 copies
11. The Silence of the Lambs (1991) - Thriller - 2 copies
12. Saving Private Ryan (1998) - War - 2 copies
13. Interstellar (2014) - Sci-Fi - 3 copies
14. The Departed (2006) - Crime - 2 copies
15. Gladiator (2000) - Action - 2 copies

## Error Handling & Logging

### Logging Levels
- **INFO**: Successful operations, initialization progress
- **WARNING**: Non-critical failures (individual movie creation)
- **SEVERE**: Critical failures (admin user creation, database connection)

### Error Recovery
- Continues initialization even if individual items fail
- Skips existing data to prevent duplicates
- Provides detailed error messages and stack traces

## Testing & Validation

### Database State Checks
1. Check if admin user exists before creation
2. Check movie count before adding sample data
3. Validate all entities before persistence
4. Log success/failure counts

### Manual Testing Steps
1. Clean database
2. Start application
3. Check logs for initialization messages
4. Verify admin login works
5. Verify movies appear in main page
6. Restart application (should skip initialization)

## Security Considerations

### Password Security
- Uses BCrypt hashing with 12 rounds
- Admin password meets validation requirements (letters + numbers)
- Password is hashed before storage, never stored in plain text

### Production Security
- Change default admin password immediately
- Use environment variables for sensitive configuration
- Consider disabling initialization in production environments

## Implementation Order

1. Create `DatabaseInitializationService.java`
2. Create `DatabaseStartupBean.java` (optional)
3. Test with clean database
4. Add configuration properties
5. Test initialization behavior
6. Document and deploy

## Switch to Code Mode
Once this plan is approved, switch to Code mode to implement the actual Java files and test the initialization system.