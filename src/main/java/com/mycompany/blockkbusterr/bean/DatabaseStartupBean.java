package com.mycompany.blockkbusterr.bean;

import com.mycompany.blockkbusterr.service.DatabaseInitializationService;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import java.util.logging.Logger;

/**
 * Startup bean that triggers database initialization on application startup.
 * This bean is automatically instantiated when the application starts up.
 */
@Singleton
@Startup
public class DatabaseStartupBean {
    
    private static final Logger logger = Logger.getLogger(DatabaseStartupBean.class.getName());
    
    @Inject
    private DatabaseInitializationService initService;
    
    @PostConstruct
    public void initialize() {
        logger.info("=== DatabaseStartupBean @PostConstruct called ===");
        
        // Check if properties are loaded
        String initProperty = System.getProperty("blockkbusterr.db.initialize");
        String adminUsername = System.getProperty("blockkbusterr.admin.username");
        logger.info("System property 'blockkbusterr.db.initialize': " + initProperty);
        logger.info("System property 'blockkbusterr.admin.username': " + adminUsername);
        
        // Check if DatabaseInitializationService is available
        if (initService == null) {
            logger.severe("DatabaseInitializationService is NULL - CDI injection failed!");
            return;
        } else {
            logger.info("DatabaseInitializationService injected successfully");
        }
        
        try {
            // Explicitly trigger initialization since CDI event observer might not work
            logger.info("=== Manually triggering database initialization ===");
            initService.initializeDefaultData();
            logger.info("=== Database initialization completed via startup bean ===");
        } catch (Exception e) {
            logger.severe("Database startup initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Manual trigger for database initialization (for admin purposes)
     */
    public void reinitializeDatabase() {
        logger.info("Manual database reinitialization requested...");
        try {
            initService.initializeDefaultData();
            logger.info("Manual database reinitialization completed.");
        } catch (Exception e) {
            logger.severe("Manual database reinitialization failed: " + e.getMessage());
            throw new RuntimeException("Database reinitialization failed", e);
        }
    }
}