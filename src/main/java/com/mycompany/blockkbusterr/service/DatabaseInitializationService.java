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
    
    // Configuration properties
    private static final String INIT_PROPERTY = "blockkbusterr.db.initialize";
    private static final String ADMIN_USERNAME_PROPERTY = "blockkbusterr.admin.username";
    private static final String ADMIN_PASSWORD_PROPERTY = "blockkbusterr.admin.password";
    
    @Inject
    private UserRepository userRepository;
    
    @Inject
    private MovieRepository movieRepository;
    
    /**
     * Initialize database on application startup
     */
    public void onApplicationStart(@Observes @Initialized(ApplicationScoped.class) Object init) {
        if (!shouldInitialize()) {
            logger.info("Database initialization disabled by configuration.");
            return;
        }
        
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
    public void initializeDefaultData() {
        initializeAdminUser();
        initializeSampleMovies();
    }
    
    /**
     * Create default admin user if not exists
     */
    private void initializeAdminUser() {
        String adminUsername = getAdminUsername();
        
        if (userRepository.existsByUsername(adminUsername)) {
            logger.info("Admin user '" + adminUsername + "' already exists, skipping creation.");
            return;
        }
        
        try {
            User admin = new User();
            admin.setFirstName("System");
            admin.setLastName("Administrator");
            admin.setEmail("admin@blockkbusterr.com");
            admin.setUsername(adminUsername);
            admin.setPassword(PasswordUtil.hashPassword(getAdminPassword()));
            admin.setRole(UserRole.ADMIN);
            admin.setActive(true);
            admin.setCreatedAt(LocalDateTime.now());
            
            userRepository.save(admin);
            logger.info("Default admin user created successfully: " + adminUsername);
            
        } catch (Exception e) {
            logger.severe("Failed to create admin user: " + e.getMessage());
            throw new RuntimeException("Critical error: Could not create admin user", e);
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
                "https://example.com/privateryan.jpg"),
                
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
    
    // Configuration methods
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
}