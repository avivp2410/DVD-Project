# Command Line Setup Instructions

## Step 1: Copy MySQL Driver
```cmd
copy "%USERPROFILE%\.m2\repository\mysql\mysql-connector-java\8.0.33\mysql-connector-java-8.0.33.jar" "C:\glassfish7\glassfish\lib\"
```

## Step 2: Create JDBC Connection Pool
```cmd
cd C:\glassfish7\bin
asadmin create-jdbc-connection-pool --datasourceclassname com.mysql.cj.jdbc.MysqlDataSource --restype javax.sql.DataSource --property user=root:password=YOUR_PASSWORD_HERE:serverName=localhost:portNumber=3306:databaseName=blockkbusterr mysql-pool
```

## Step 3: Create JDBC Resource
```cmd
asadmin create-jdbc-resource --connectionpoolid mysql-pool jdbc/BlockkBusterrDS
```

## Step 4: Test Connection
```cmd
asadmin ping-connection-pool mysql-pool
```

## Step 5: Restart GlassFish
```cmd
asadmin restart-domain domain1
```

## Step 6: Rebuild and Deploy
```cmd
cd C:\TestRepos\DVD\DVD-Project
mvn clean package
C:\glassfish7\bin\asadmin deploy target\BlockkBusterr-1.0-SNAPSHOT.war
```

## Step 7: Access Application
- **URL:** http://localhost:8080/BlockkBusterr-1.0-SNAPSHOT/login.xhtml

**Important:** Replace `YOUR_PASSWORD_HERE` with your actual MySQL password in Step 2.