package com.example.test.demo;

import com.logging.framework.LoggerFactory;
import com.logging.framework.specialized.RestApiLogger;
import jakarta.annotation.PostConstruct;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class UserController {
    private static final RestApiLogger logger = LoggerFactory.getRestApiLogger(UserController.class);

    @GetMapping("/users/{id}")
    public String getUser(@PathVariable String id, HttpServletRequest request) {
        logger.logRequest(request)
                .with("user_id", id)
                .info("Fetching user details");

        // Business logic
        String userData = fetchUserData(id);

        logger.with("response_size", userData.length())
                .info("User data retrieved");

        return userData;
    }

    private String fetchUserData(String id) {
        return "User data for " + id;
    }

    @PostConstruct
    public void init() {
        com.logging.framework.context.ApplicationContext context =
                com.logging.framework.context.ApplicationContext.getInstance();
        context.setApplicationName("UserService");
        context.setEnvironment("production");

        new com.logging.framework.config.LoggingConfiguration()
                .withFileOutput("logs/user-service.log", true)
                .includeStackTrace(true);
    }
}
