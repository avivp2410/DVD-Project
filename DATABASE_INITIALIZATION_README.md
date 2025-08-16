# Database Initialization System

This document explains how to use the automatic database initialization system for the Blockk Busterr DVD rental application.

## Overview

The database initialization system automatically populates your database with:
- **1 Default Admin User** for immediate access
- **15 Sample Movies** across various genres for testing and demonstration

## Quick Start

1. **Clean Database**: Ensure your MySQL database is clean (no existing users/movies)
2. **Start Application**: Run the application using TomEE
3. **Automatic Setup**: The system will automatically create default data on first startup
4. **Login**: Use `admin` / `admin123` to access the admin panel

## Default Data Created

### Admin User
- **Username**: `admin`
- **Password**: `admin123`
- **Email**: `admin@blockkbusterr.com`
- **Role**: Administrator
- **Status**: Active

### Sample Movies (15 movies)
- The Shawshank Redemption (1994) - Drama - 3 copies
- The Godfather (1972) - Crime - 2 copies
- The Dark Knight (2008) - Action - 4 copies
- Pulp Fiction (1994) - Crime - 2 copies
- Forrest Gump (1994) - Drama - 3 copies
- Inception (2010) - Sci-Fi - 3 copies
- The Matrix (1999) - Sci-Fi - 2 copies
- Goodfellas (1990) - Crime - 2 copies
- LOTR: Fellowship of the Ring (2001) - Fantasy - 2 copies
- Star Wars: A New Hope (1977) - Sci-Fi - 3 copies
- The Silence of the Lambs (1991) - Thriller - 2 copies
- Saving Private Ryan (1998) - War - 2 copies
- Interstellar (2014) - Sci-Fi - 3 copies
- The Departed (2006) - Crime - 2 copies
- Gladiator (2000) - Action - 2 copies

## Configuration Options

### System Properties
You can customize the initialization behavior using system properties:

```bash
# Disable initialization
-Dblockkbusterr.db.initialize=false

# Custom admin credentials
-Dblockkbusterr.admin.username=myadmin
-Dblockkbusterr.admin.password=mypassword123
```

### Configuration File
Edit `src/main/resources/database-init.properties`:

```properties
# Enable/disable initialization
blockkbusterr.db.initialize=true

# Admin credentials
blockkbusterr.admin.username=admin
blockkbusterr.admin.password=admin123
```

### TomEE Configuration
Add to `conf/system.properties`:

```properties
blockkbusterr.db.initialize=true
blockkbusterr.admin.username=admin
blockkbusterr.admin.password=admin123
```

## How It Works

### Automatic Initialization
1. **Application Startup**: [`DatabaseInitializationService`](src/main/java/com/mycompany/blockkbusterr/service/DatabaseInitializationService.java) observes application startup
2. **Smart Detection**: Checks if admin user exists and if movies table is empty
3. **Safe Creation**: Only creates data if it doesn't already exist
4. **Secure Passwords**: Uses BCrypt hashing for password storage
5. **Transaction Safety**: All operations are wrapped in transactions

### Components
- **[`DatabaseInitializationService`](src/main/java/com/mycompany/blockkbusterr/service/DatabaseInitializationService.java)**: Main initialization logic
- **[`DatabaseStartupBean`](src/main/java/com/mycompany/blockkbusterr/bean/DatabaseStartupBean.java)**: Startup trigger bean
- **[`database-init.properties`](src/main/resources/database-init.properties)**: Configuration file

## Security Features

### Password Security
- Admin password is hashed using BCrypt with 12 rounds
- Default password meets validation requirements (letters + numbers)
- Stored hash is never exposed in logs

### Production Safety
- Initialization can be disabled for production environments
- Default credentials should be changed immediately
- Configuration supports environment-specific settings

## Testing & Validation

### Verify Installation
1. **Check Logs**: Look for initialization messages in server logs
2. **Login Test**: Try logging in with admin credentials
3. **Movie List**: Navigate to main page to see sample movies
4. **Admin Panel**: Access admin features with admin account

### Expected Log Messages
```
INFO: Starting database initialization...
INFO: Default admin user created successfully: admin
INFO: Creating sample movie dataset...
INFO: Created movie: The Shawshank Redemption
...
INFO: Sample movie creation completed. Successfully created 15 out of 15 movies.
INFO: Database initialization completed successfully.
```

### Troubleshooting

#### Common Issues

**Issue**: "Admin user already exists"
- **Solution**: This is normal - the system skips creation if admin exists

**Issue**: "Movies already exist, skipping sample data"
- **Solution**: Normal behavior - won't duplicate existing movies

**Issue**: Database connection errors
- **Solution**: Check MySQL is running and [`tomee.xml`](src/main/webapp/WEB-INF/tomee.xml) configuration

**Issue**: Password validation errors
- **Solution**: Ensure admin password has letters and numbers (min 6 chars)

#### Reset Database
To start fresh:
1. Stop the application
2. Clear/recreate the database tables
3. Restart the application
4. Initialization will run automatically

## Manual Operations

### Disable Initialization
Set system property: `-Dblockkbusterr.db.initialize=false`

### Force Reinitialization
Currently, the system won't reinitialize if data exists. To force reinit:
1. Clear existing data manually
2. Restart application

### Custom Admin Credentials
Set system properties before startup:
```bash
-Dblockkbusterr.admin.username=youradmin
-Dblockkbusterr.admin.password=yourpassword123
```

## Development Notes

### Adding New Sample Data
Edit [`DatabaseInitializationService.java`](src/main/java/com/mycompany/blockkbusterr/service/DatabaseInitializationService.java) and add new movies to the `sampleMovies` array.

### Changing Default Behavior
Modify the configuration properties or system property checks in the service class.

### Integration with CI/CD
- Set `blockkbusterr.db.initialize=false` in production deployments
- Use environment-specific configuration files
- Consider using database migration tools for production

## File Structure

```
src/main/java/com/mycompany/blockkbusterr/
├── service/DatabaseInitializationService.java
├── bean/DatabaseStartupBean.java
└── ...

src/main/resources/
├── database-init.properties
└── ...

DATABASE_INITIALIZATION_README.md
database-initialization-plan.md
```

## Support

For issues with database initialization:
1. Check server logs for error messages
2. Verify database connectivity
3. Ensure proper permissions for database operations
4. Review configuration settings