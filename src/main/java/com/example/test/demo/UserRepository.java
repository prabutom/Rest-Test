package com.example.test.demo;

import com.logging.framework.LoggerFactory;
import com.logging.framework.specialized.DatabaseLogger;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.sql.*;

@Service
public class UserRepository {
    private static final DatabaseLogger logger = LoggerFactory.getDatabaseLogger(UserRepository.class);
    private Connection connection;

    public User getUser(int id) throws SQLException {
        logger.logQueryStart("SELECT * FROM users WHERE id = ?", new Object[]{id});

        long startTime = System.currentTimeMillis();
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE id = ?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            long duration = System.currentTimeMillis() - startTime;
            logger.logQueryExecution("SELECT * FROM users WHERE id = ?", duration);

            if (rs.next()) {
                User user = new User(rs.getInt("id"), rs.getString("name"));
                logger.info("User found: " + user.getName());
                return user;
            }
            return null;
        } catch (SQLException e) {
            logger.error("Database error", e);
            throw e;
        }
    }

    @PostConstruct
    public void init() throws SQLException {
        // Initialize logging
        com.logging.framework.context.ApplicationContext context =
                com.logging.framework.context.ApplicationContext.getInstance();
        context.setApplicationName("UserDatabase");
        context.setEnvironment("production");

        new com.logging.framework.config.LoggingConfiguration()
                .withFileOutput("logs/db-service.log", true);

        // Initialize DB connection
        connection = DriverManager.getConnection("jdbc:h2:mem:test", "sa", "");
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE users(id INT PRIMARY KEY, name VARCHAR(100))");
            stmt.execute("INSERT INTO users VALUES(1, 'John Doe')");
        }
    }
}
