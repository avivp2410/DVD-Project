@echo off
echo Setting up JDBC DataSource for BlockkBusterr in GlassFish...
echo.

echo.
pause

echo Creating MySQL connection pool...
C:\glassfish7\bin\asadmin create-jdbc-connection-pool --datasourceclassname com.mysql.cj.jdbc.MysqlDataSource --restype javax.sql.DataSource --property user=root:password=12345678:serverName=localhost:portNumber=3306:databaseName=blockkbusterr mysql-pool

echo.
echo Creating JDBC resource (fixed JNDI name)...
C:\glassfish7\bin\asadmin create-jdbc-resource --connectionpoolid mysql-pool jdbc/BlockkBusterrDS

echo.
echo Testing connection...
C:\glassfish7\bin\asadmin ping-connection-pool mysql-pool

echo.
echo Copying MySQL driver to GlassFish...
copy "%USERPROFILE%\.m2\repository\mysql\mysql-connector-java\8.0.33\mysql-connector-java-8.0.33.jar" "C:\glassfish7\glassfish\lib\"

echo.
echo Restarting GlassFish...
C:\glassfish7\bin\asadmin restart-domain domain1

echo.
echo Now rebuild and redeploy the application:
echo mvn clean package
echo asadmin deploy target\BlockkBusterr-1.0-SNAPSHOT.war
echo.
pause