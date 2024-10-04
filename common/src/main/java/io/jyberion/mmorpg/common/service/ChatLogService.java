package io.jyberion.mmorpg.common.service;

import io.jyberion.mmorpg.common.entity.ChatLog;
import io.jyberion.mmorpg.common.util.AsyncDatabaseUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.concurrent.CompletableFuture;

public class ChatLogService {

    private static SessionFactory sessionFactory = new Configuration()
            .configure("hibernate.cfg.xml")
            .buildSessionFactory();

    public static CompletableFuture<Void> saveChatLogAsync(ChatLog chatLog) {
        return CompletableFuture.runAsync(() -> {
            Transaction transaction = null;
            Session session = null;
            try {
                session = sessionFactory.openSession();
                transaction = session.beginTransaction();
                session.save(chatLog);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
                e.printStackTrace();
            } finally {
                if (session != null) session.close();
            }
        }, AsyncDatabaseUtil.getExecutor());
    }
}
