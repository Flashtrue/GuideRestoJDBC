package ch.hearc.ig.guideresto.persistence.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.function.Consumer;
import java.util.function.Function;

public class JpaUtils {

    private static EntityManagerFactory emf;
    private static EntityManager em;

    public static EntityManager getEntityManager() {
        if (em == null || !em.isOpen()) {
            if (emf == null) {
                emf = Persistence.createEntityManagerFactory("guideRestoJPA");
            }
            em = emf.createEntityManager();
        }
        return em;
    }

    public static void inTransaction(Consumer<EntityManager> consumer) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        boolean weStartedTransaction = false;

        try {
            // Ne démarre une transaction que si aucune n'est active
            if (!transaction.isActive()) {
                transaction.begin();
                weStartedTransaction = true;
            }

            consumer.accept(em);

            // Ne commit que si c'est nous qui avons démarré la transaction
            if (weStartedTransaction) {
                em.flush();
                transaction.commit();
            }
        } catch (Exception ex) {
            // Ne rollback que si c'est nous qui avons démarré la transaction
            if (weStartedTransaction && transaction.isActive()) {
                transaction.rollback();
            }
            throw ex;
        }
    }

    public static <T> T inTransactionWithResult(Function<EntityManager, T> function) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        boolean weStartedTransaction = false;

        try {
            // Ne démarre une transaction que si aucune n'est active
            if (!transaction.isActive()) {
                transaction.begin();
                weStartedTransaction = true;
            }

            T result = function.apply(em);

            // Ne commit que si c'est nous qui avons démarré la transaction
            if (weStartedTransaction) {
                em.flush();
                transaction.commit();
            }

            return result;
        } catch (Exception ex) {
            // Ne rollback que si c'est nous qui avons démarré la transaction
            if (weStartedTransaction && transaction.isActive()) {
                transaction.rollback();
            }
            throw ex;
        }
    }

    public static void closeEntityManagerFactory() {
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}