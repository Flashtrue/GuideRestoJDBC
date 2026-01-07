package ch.hearc.ig.guideresto.services;

import ch.hearc.ig.guideresto.persistence.jpa.JpaUtils;
import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Classe abstraite fournissant des services de base pour la gestion des transactions JPA.
 */
public abstract class AbstractService {
    protected static final Logger logger = LogManager.getLogger();

    /**
     * Exécute une opération dans le contexte d'une transaction JPA.
     * 
     * @param operation l'opération à exécuter avec l'EntityManager
     */
    protected void executeInTransaction(Consumer<EntityManager> operation) {
        JpaUtils.inTransaction(operation);
    }

    /**
     * Exécute une opération dans le contexte d'une transaction JPA et retourne un résultat.
     * 
     * @param <T> le type du résultat retourné
     * @param operation l'opération à exécuter avec l'EntityManager
     * @return le résultat de l'opération
     */
    protected <T> T executeInTransactionWithResult(Function<EntityManager, T> operation) {
        return JpaUtils.inTransactionWithResult(operation);
    }

    /**
     * Fournit l'EntityManager courant pour effectuer des opérations de persistance.
     * 
     * @return l'EntityManager actif
     */
    protected EntityManager em() {
        return JpaUtils.getEntityManager();
    }
}