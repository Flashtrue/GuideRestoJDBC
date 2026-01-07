package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.IBusinessObject;
import ch.hearc.ig.guideresto.persistence.jpa.JpaUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.TypedQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Classe abstraite fournissant des méthodes de mappage génériques pour les entités JPA.
 *
 * @param <T> le type de l'entité gérée par ce mapper.
 */
public abstract class AbstractMapper<T extends IBusinessObject> {

    protected static final Logger logger = LogManager.getLogger();
    private final Class<T> entityClass;

    /**
     * Constructeur protégé initialisant le mapper avec la classe d'entité correspondante.
     *
     * @param entityClass la classe de l'entité gérée par ce mapper.
     */
    protected AbstractMapper(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Récupère l'EntityManager utilisé pour les opérations JPA.
     *
     * @return l'EntityManager courant.
     */
    protected EntityManager em() {
        return JpaUtils.getEntityManager();
    }

    /**
     * Récupère une entité par son identifiant.
     *
     * @param id l'identifiant de l'entité recherchée.
     * @return l'entité correspondante, ou null si aucune entité n'est trouvée.
     */
    public T findById(int id) {
        return em().find(entityClass, id);
    }

    /**
     * Récupère toutes les entités.
     *
     * @return un ensemble de toutes les entités.
     */
    public Set<T> findAll() {
        TypedQuery<T> query = em().createQuery(
                "SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass);
        return new LinkedHashSet<>(query.getResultList());
    }

    /**
     * Crée une nouvelle entité dans la base de données.
     *
     * @param object l'entité à créer.
     * @return l'entité créée, ou null en cas d'erreur.
     */
    public T create(T object) {
        if (object == null) {
            return null;
        }
        try {
            JpaUtils.inTransaction(em -> em.persist(object));
            return object;
        } catch (RuntimeException ex) {
            logger.error("JPA persist error", ex);
            return null;
        }
    }

    /**
     * Met à jour une entité existante dans la base de données.
     *
     * @param object l'entité à mettre à jour.
     * @return true si la mise à jour a réussi, false sinon.
     */
    public boolean update(T object) {
        if (object == null || object.getId() == null) {
            return false;
        }
        try {
            JpaUtils.inTransaction(em -> em.merge(object));
            return true;
        } catch (OptimisticLockException ex) {
            logger.warn("JPA optimistic lock error", ex);
            return false;
        } catch (RuntimeException ex) {
            logger.error("JPA merge error", ex);
            return false;
        }
    }

    /**
     * Supprime une entité de la base de données.
     *
     * @param object l'entité à supprimer.
     * @return true si la suppression a réussi, false sinon.
     */
    public boolean delete(T object) {
        if (object == null || object.getId() == null) {
            return false;
        }
        try {
            JpaUtils.inTransaction(em -> {
                T managed = em.contains(object) ? object : em.merge(object);
                em.remove(managed);
            });
            return true;
        } catch (RuntimeException ex) {
            logger.error("JPA remove error", ex);
            return false;
        }
    }

    /**
     * Supprime une entité par son identifiant.
     *
     * @param id l'identifiant de l'entité à supprimer.
     * @return true si la suppression a réussi, false sinon.
     */
    public boolean deleteById(int id) {
        AtomicBoolean deleted = new AtomicBoolean(false);
        try {
            JpaUtils.inTransaction(em -> {
                T entity = em.find(entityClass, id);
                if (entity != null) {
                    em.remove(entity);
                    deleted.set(true);
                }
            });
            return deleted.get();
        } catch (RuntimeException ex) {
            logger.error("JPA remove error", ex);
            return false;
        }
    }

    /**
     * Vérifie si une entité existe dans la base de données par son identifiant.
     *
     * @param id l'identifiant de l'entité.
     * @return true si l'entité existe, false sinon.
     */
    public boolean exists(int id) {
        return findById(id) != null;
    }

    /**
     * Compte le nombre total d'entités dans la base de données.
     *
     * @return le nombre total d'entités.
     */
    public int count() {
        TypedQuery<Long> query = em().createQuery(
                "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e", Long.class);
        Long value = query.getSingleResult();
        return value != null ? value.intValue() : 0;
    }
}
