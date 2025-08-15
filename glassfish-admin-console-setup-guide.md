# GlassFish Admin Console Setup Guide

## Step-by-Step Data Source Configuration

### 1. Access GlassFish Admin Console
Open your browser and go to: **http://localhost:4848**

### 2. Add MySQL Driver to GlassFish
Before creating the data source, copy the MySQL driver:
```cmd
copy "%USERPROFILE%\.m2\repository\mysql\mysql-connector-java\8.0.33\mysql-connector-java-8.0.33.jar" "C:\glassfish7\glassfish\lib\"
```

### 3. Create JDBC Connection Pool
1. Navigate to: **Resources → JDBC → JDBC Connection Pools**
2. Click **New**
3. Fill in the details:
   - **Pool Name:** `mysql-pool`
   - **Resource Type:** `javax.sql.DataSource`
   - **Database Driver Vendor:** `MySql`
4. Click **Next**
5. In the **Additional Properties** section, add these properties:
   - **serverName:** `localhost`
   - **portNumber:** `3306`
   - **databaseName:** `blockkbusterr`
   - **user:** `root` (or your MySQL username)
   - **password:** `[YOUR_MYSQL_PASSWORD]`
   - **useSSL:** `false`
   - **serverTimezone:** `UTC`
6. Click **Finish**

### 4. Test the Connection Pool
1. In the **JDBC Connection Pools** list, click on `mysql-pool`
2. Click the **Ping** button
3. You should see: "Ping Succeeded"

### 5. Create JDBC Resource
1. Navigate to: **Resources → JDBC → JDBC Resources**
2. Click **New**
3. Fill in the details:
   - **JNDI Name:** `jdbc/BlockkBusterrDS`
   - **Pool Name:** `mysql-pool`
4. Click **OK**

### 6. Restart GlassFish
```cmd
cd C:\glassfish7\bin
asadmin restart-domain domain1
```

### 7. Rebuild and Deploy Application
```cmd
mvn clean package
asadmin deploy target\BlockkBusterr-1.0-SNAPSHOT.war
```

### 8. Access the Application
- **Login Page:** http://localhost:8080/BlockkBusterr-1.0-SNAPSHOT/login.xhtml
- **Main Application:** http://localhost:8080/BlockkBusterr-1.0-SNAPSHOT/

## Important Notes
- **JNDI Name must be exactly:** `jdbc/BlockkBusterrDS` (no colons allowed)
- **MySQL database `blockkbusterr` must exist**
- **Replace `[YOUR_MYSQL_PASSWORD]` with your actual MySQL password**
- **Tables will be created automatically on first run**