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
        return restaurantTypeMapper.create(type);
    }

    public boolean update(RestaurantType type) {
        return restaurantTypeMapper.update(type);
    }

    public boolean delete(RestaurantType type) {
        return restaurantTypeMapper.delete(type);
    }
}