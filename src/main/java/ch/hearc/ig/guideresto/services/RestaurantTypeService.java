package ch.hearc.ig.guideresto.services;

import ch.hearc.ig.guideresto.business.RestaurantType;
import ch.hearc.ig.guideresto.persistence.RestaurantTypeMapper;

import java.sql.SQLException;
import java.util.Set;

public class RestaurantTypeService extends AbstractService {

    private final RestaurantTypeMapper restaurantTypeMapper;

    public RestaurantTypeService() {
        this.restaurantTypeMapper = new RestaurantTypeMapper();
    }

    public Set<RestaurantType> getAll() {
        return restaurantTypeMapper.findAll();
    }

    public RestaurantType findById(int id) {
        return restaurantTypeMapper.findById(id);
    }

    public RestaurantType findByLabel(String label) {
        Set<RestaurantType> types = getAll();

        for (RestaurantType type : types) {
            if (type.getLabel().equalsIgnoreCase(label)) {
                return type;
            }
        }

        return null;
    }

    public RestaurantType create(RestaurantType type) {
        try {
            executeInTransaction(() -> {
                restaurantTypeMapper.create(type);
            });
            return type;
        } catch (SQLException e) {
            logger.error("Erreur lors de la création du type de restaurant", e);
            return null;
        }
    }

    public boolean update(RestaurantType type) {
        try {
            executeInTransaction(() -> {
                restaurantTypeMapper.update(type);
            });
            return true;
        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour du type de restaurant", e);
            return false;
        }
    }

    public boolean delete(RestaurantType type) {
        try {
            executeInTransaction(() -> {
                restaurantTypeMapper.delete(type);
            });
            return true;
        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression du type de restaurant", e);
            return false;
        }
    }
}