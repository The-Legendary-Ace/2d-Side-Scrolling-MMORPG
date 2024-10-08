package io.jyberion.mmorpg.login.service;

import io.jyberion.mmorpg.login.entity.Accounts;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.PersistenceException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class UserService {

    private SessionFactory sessionFactory;

    public UserService() {
        sessionFactory = new Configuration().configure().addAnnotatedClass(Accounts.class).buildSessionFactory();
    }

    public CompletableFuture<Boolean> authenticateUserAsync(String username, String password) {
        return CompletableFuture.supplyAsync(() -> {
            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();

                // Query the Accounts entity
                Accounts account = session.createQuery("FROM Accounts WHERE username = :username", Accounts.class)
                        .setParameter("username", username)
                        .uniqueResult();

                session.getTransaction().commit();

                if (account != null && account.getPassword().equals(password)) {
                    return true;
                } else {
                    return false;
                }
            } catch (PersistenceException e) {
                // Log the error and return false
                System.err.println("Failed to authenticate user");
                e.printStackTrace();
                return false;
            }
        });
    }

    public Optional<BanStatus> checkBanStatus(String username) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            // Query the account
            Accounts account = session.createQuery("FROM Accounts WHERE username = :username", Accounts.class)
                    .setParameter("username", username)
                    .uniqueResult();

            session.getTransaction().commit();

            if (account != null) {
                int banStatus = account.getBanned();
                if (banStatus == 1) {  // Temporarily banned
                    Date banExpireDate = account.getBanExpireDate();
                    if (banExpireDate != null) {
                        LocalDateTime banExpire = banExpireDate.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime();

                        if (LocalDateTime.now().isBefore(banExpire)) {
                            return Optional.of(new BanStatus(true, "Temporary ban", account.getBanReason(), banExpire));
                        }
                    }
                } else if (banStatus == 2) {  // Permanently banned
                    return Optional.of(new BanStatus(true, "Permanent ban", account.getBanReason(), null));
                }
            }
            return Optional.of(new BanStatus(false, null, null, null));  // Not banned
        } catch (PersistenceException e) {
            System.err.println("Error checking ban status");
            e.printStackTrace();
            return Optional.empty();  // Handle failure scenario
        }
    }

    // Helper class to return ban status details
    public static class BanStatus {
        private boolean isBanned;
        private String banType;
        private String banReason;
        private LocalDateTime banExpireDate;

        public BanStatus(boolean isBanned, String banType, String banReason, LocalDateTime banExpireDate) {
            this.isBanned = isBanned;
            this.banType = banType;
            this.banReason = banReason;
            this.banExpireDate = banExpireDate;
        }

        public boolean isBanned() {
            return isBanned;
        }

        public String getBanType() {
            return banType;
        }

        public String getBanReason() {
            return banReason;
        }

        public LocalDateTime getBanExpireDate() {
            return banExpireDate;
        }
    }
}
