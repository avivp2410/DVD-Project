@echo off
echo Starting BlockkBusterr with TomEE embedded server...
echo.
echo Make sure MySQL is running with:
echo - Database: blockkbusterr
echo - Username/Password configured in persistence.xml
echo.
echo Starting server... (This may take a few moments)
mvn -f pom-tomee.xml clean package tomee:run
