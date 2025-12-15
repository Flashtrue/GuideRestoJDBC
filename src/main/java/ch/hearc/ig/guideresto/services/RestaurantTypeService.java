package ch.hearc.ig.guideresto.services;

import ch.hearc.ig.guideresto.business.RestaurantType;
import ch.hearc.ig.guideresto.persistence.RestaurantTypeMapper;

import java.util.Set;

public class RestaurantTypeService extends AbstractService {

    private final RestaurantTypeMapper restaurantTypeMapper = new RestaurantTypeMapper();

    public Set<RestaurantType> getAll() {
        return restaurantTypeMapper.findAll();
    }

    public RestaurantType findById(int id) {
        return restaurantTypeMapper.findById(id);
    }

    public RestaurantType findByLabel(String label) {
        Set<RestaurantType> types = restaurantTypeMapper.findByLabel(label);
        return types.isEmpty() ? null : types.iterator().next();
    }

    public RestaurantType create(RestaurantType type) {
        try {
            return executeInTransactionWithResult(em -> {
                em.persist(type);
                return type;
            });
        } catch (Exception e) {
            logger.error("Erreur lors de la création du type de restaurant", e);
            return null;
        }
    }

    public boolean update(RestaurantType type) {
        try {
            executeInTransaction(em -> em.merge(type));
            return true;
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du type de restaurant", e);
            return false;
        }
    }

    public boolean delete(RestaurantType type) {
        try {
            executeInTransaction(em -> {
                RestaurantType managed = em.contains(type) ? type : em.merge(type);
                em.remove(managed);
            });
            return true;
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression du type de restaurant", e);
            return false;
        }
    }
}