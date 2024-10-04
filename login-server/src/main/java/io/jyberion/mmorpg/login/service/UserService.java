package io.jyberion.mmorpg.login.service;

import io.jyberion.mmorpg.common.entity.User;
import io.jyberion.mmorpg.common.security.PasswordUtil;
import io.jyberion.mmorpg.common.util.AsyncDatabaseUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static SessionFactory sessionFactory;

    public UserService() {
        if (sessionFactory == null) {
            sessionFactory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(User.class)
                    .buildSessionFactory();
        }
    }

    public boolean testDatabaseConnection() {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.createNativeQuery("SELECT 1").getSingleResult();
            logger.info("Database connection successful.");
            return true;
        } catch (Exception e) {
            logger.error("Failed to connect to the database.", e);
            return false;
        } finally {
            if (session != null) session.close();
        }
    }

    public CompletableFuture<Boolean> authenticateUserAsync(String username, String password) {
        return CompletableFuture.supplyAsync(() -> {
            Session session = null;
            try {
                session = sessionFactory.openSession();
                User user = (User) session.createQuery("FROM User WHERE username = :username")
                        .setParameter("username", username)
                        .uniqueResult();

                if (user != null && PasswordUtil.checkPassword(password, user.getPasswordHash())) {
                    return true;
                }
            } catch (Exception e) {
                logger.error("Failed to authenticate user", e);
            } finally {
                if (session != null) session.close();
            }
            return false;
        }, AsyncDatabaseUtil.getExecutor());
    }

    public void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
