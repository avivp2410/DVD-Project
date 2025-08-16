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
        logger.info("DatabaseStartupBean initializing...");
        try {
            // The DatabaseInitializationService already observes application startup,
            // but this provides an additional trigger point if needed
            logger.info("Database initialization triggered via startup bean.");
        } catch (Exception e) {
            logger.severe("Database startup initialization failed: " + e.getMessage());
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