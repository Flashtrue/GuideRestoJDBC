package ch.hearc.ig.guideresto.services;

import ch.hearc.ig.guideresto.persistence.jpa.JpaUtils;
import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractService {
    protected static final Logger logger = LogManager.getLogger();

    protected void executeInTransaction(Consumer<EntityManager> operation) {
        JpaUtils.inTransaction(operation);
    }

    protected <T> T executeInTransactionWithResult(Function<EntityManager, T> operation) {
        return JpaUtils.inTransactionWithResult(operation);
    }

    protected EntityManager em() {
        return JpaUtils.getEntityManager();
    }
}