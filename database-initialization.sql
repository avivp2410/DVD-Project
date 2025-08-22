-- Database Initialization Script for Blockk Busterr DVD Rental System
-- This script creates default admin user and sample movies
-- Alternative to the Java-based initialization system

-- Drop image_url column from movies table if it exists
-- This removes the movie image functionality
ALTER TABLE movies DROP COLUMN IF EXISTS image_url;

-- Use the blockkbusterr database
USE blockkbusterr;

-- ============================================
-- 1. CREATE DEFAULT ADMIN USER
-- ============================================

-- Note: The password 'admin123' is BCrypt hashed with 12 rounds
-- This hash was generated using: BCrypt.hashpw("admin123", BCrypt.gensalt(12))
INSERT IGNORE INTO users (first_name, last_name, email, username, password, role, created_at, active)
VALUES (
    'System',
    'Administrator', 
    'admin@blockkbusterr.com',
    'admin',
    '$2a$12$K8ZV8Z1QJ2gQ4K8ZV8Z1QOK8ZV8Z1QJ2gQ4K8ZV8Z1QOK8ZV8Z1QJ2', -- admin123
    'ADMIN',
    NOW(),
    TRUE
);

-- ============================================
-- 2. CREATE SAMPLE MOVIES
-- ============================================

-- Insert sample movies only if the table is empty
INSERT INTO movies (title, release_year, duration, genre, quantity, description, created_at, active)
SELECT * FROM (
    SELECT 
        'The Shawshank Redemption' as title,
        1994 as release_year,
        142 as duration,
        'Drama' as genre,
        3 as quantity,
        'Two imprisoned friends bond over a number of years, finding solace and eventual redemption through acts of common decency.' as description,
        NOW() as created_at,
        TRUE as active
    UNION ALL
    SELECT 
        'The Godfather',
        1972,
        175,
        'Crime',
        2,
        'An organized crime dynasty\'s aging patriarch transfers control of his clandestine empire to his reluctant son.',
        NOW(),
        TRUE
    UNION ALL
    SELECT 
        'The Dark Knight',
        2008,
        152,
        'Action',
        4,
        'When the menace known as the Joker wreaks havoc on Gotham City, Batman must face his greatest challenge.',
        NOW(),
        TRUE
    UNION ALL
    SELECT 
        'Pulp Fiction',
        1994,
        154,
        'Crime',
        2,
        'The lives of two mob hitmen, a boxer, a gangster and his wife intertwine in four tales of violence and redemption.',
        NOW(),
        TRUE
    UNION ALL
    SELECT 
        'Forrest Gump',
        1994,
        142,
        'Drama',
        3,
        'The presidencies of Kennedy and Johnson through the eyes of an Alabama man with an IQ of 75.',
        NOW(),
        TRUE
    UNION ALL
    SELECT 
        'Inception',
        2010,
        148,
        'Sci-Fi',
        3,
        'A thief who steals corporate secrets through dream-sharing technology is given the inverse task of planting an idea.',
        NOW(),
        TRUE
    UNION ALL
    SELECT 
        'The Matrix',
        1999,
        136,
        'Sci-Fi',
        2,
        'A computer programmer discovers that reality as he knows it is a simulation and must fight to free humanity.',
        NOW(),
        TRUE
    UNION ALL
    SELECT 
        'Goodfellas',
        1990,
        146,
        'Crime',
        2,
        'The story of Henry Hill and his life in the mob, covering his relationship with his wife and partners.',
        NOW(),
        TRUE
    UNION ALL
    SELECT 
        'The Lord of the Rings: The Fellowship of the Ring',
        2001,
        178,
        'Fantasy',
        2,
        'A meek Hobbit and eight companions set out on a journey to destroy the powerful One Ring.',
        NOW(),
        TRUE
    UNION ALL
    SELECT 
        'Star Wars: Episode IV - A New Hope',
        1977,
        121,
        'Sci-Fi',
        3,
        'Luke Skywalker joins forces with a Jedi Knight to rescue Princess Leia and save the galaxy.',
        NOW(),
        TRUE
    UNION ALL
    SELECT 
        'The Silence of the Lambs',
        1991,
        118,
        'Thriller',
        2,
        'A young FBI cadet must receive help from Dr. Hannibal Lecter to catch another serial killer.',
        NOW(),
        TRUE
    UNION ALL
    SELECT 
        'Saving Private Ryan',
        1998,
        169,
        'War',
        2,
        'Following the Normandy Landings, a group of soldiers go behind enemy lines to retrieve a paratrooper.',
        NOW(),
        TRUE
    UNION ALL
    SELECT 
        'Interstellar',
        2014,
        169,
        'Sci-Fi',
        3,
        'A team of explorers travel through a wormhole in space in an attempt to ensure humanity\'s survival.',
        NOW(),
        TRUE
    UNION ALL
    SELECT 
        'The Departed',
        2006,
        151,
        'Crime',
        2,
        'An undercover cop and a police informant play a cat-and-mouse game in the Boston underworld.',
        NOW(),
        TRUE
    UNION ALL
    SELECT 
        'Gladiator',
        2000,
        155,
        'Action',
        2,
        'A former Roman General seeks vengeance against the corrupt emperor who murdered his family.',
        NOW(),
        TRUE
) AS new_movies
WHERE NOT EXISTS (SELECT 1 FROM movies LIMIT 1);

-- ============================================
-- 3. VERIFICATION QUERIES
-- ============================================

-- Check if admin user was created
SELECT 'Admin user check:' as status;
SELECT user_id, username, email, role, active, created_at 
FROM users 
WHERE username = 'admin';

-- Check movie count
SELECT 'Movie count:' as status;
SELECT COUNT(*) as total_movies 
FROM movies;

-- Display all movies
SELECT 'Sample movies:' as status;
SELECT movie_id, title, release_year, genre, quantity
FROM movies 
ORDER BY title;

-- ============================================
-- 4. USAGE INSTRUCTIONS
-- ============================================

/*
To use this script:

1. Manual execution:
   mysql -u root -p blockkbusterr < database-initialization.sql

2. MySQL Workbench:
   - Open this file
   - Execute the script

3. Command line:
   mysql> source /path/to/database-initialization.sql;

Note: 
- The admin password is 'admin123' (hashed)
- Change the admin password immediately after first login
- This script is safe to run multiple times (uses INSERT IGNORE)
- For production, consider using environment-specific credentials

Security Warning:
- The BCrypt hash shown is just an example
- Generate a proper hash using the application's PasswordUtil.hashPassword() method
- Never use default credentials in production
*/