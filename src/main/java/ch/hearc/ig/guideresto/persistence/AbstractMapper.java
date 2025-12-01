package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.IBusinessObject;
import ch.hearc.ig.guideresto.persistence.jpa.JpaUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractMapper<T extends IBusinessObject> {

    protected static final Logger logger = LogManager.getLogger();
    private final Class<T> entityClass;

    protected AbstractMapper(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected EntityManager em() {
        return JpaUtils.getEntityManager();
    }

    public T findById(int id) {
        return em().find(entityClass, id);
    }

    public Set<T> findAll() {
        TypedQuery<T> query = em().createQuery(
                "SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass);
        return new LinkedHashSet<>(query.getResultList());
    }

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

    public boolean update(T object) {
        if (object == null || object.getId() == null) {
            return false;
        }
        try {
            JpaUtils.inTransaction(em -> em.merge(object));
            return true;
        } catch (RuntimeException ex) {
            logger.error("JPA merge error", ex);
            return false;
        }
    }

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

    public boolean exists(int id) {
        return findById(id) != null;
    }

    public int count() {
        TypedQuery<Long> query = em().createQuery(
                "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e", Long.class);
        Long value = query.getSingleResult();
        return value != null ? value.intValue() : 0;
    }
}
