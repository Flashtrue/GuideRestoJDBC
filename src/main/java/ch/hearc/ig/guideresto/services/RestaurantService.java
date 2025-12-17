package ch.hearc.ig.guideresto.services;

import ch.hearc.ig.guideresto.business.*;
import ch.hearc.ig.guideresto.persistence.*;

import java.util.Set;

public class RestaurantService extends AbstractService {

    private final RestaurantMapper restaurantMapper = new RestaurantMapper();
    private final CityMapper cityMapper = new CityMapper();

    public Set<Restaurant> getAllRestaurants() {
        return restaurantMapper.findAll();
    }

    public Restaurant findById(int id) {
        return restaurantMapper.findById(id);
    }

    public Set<Restaurant> findByName(String name) {
        return restaurantMapper.findByName(name);
    }

    public Set<Restaurant> findByCity(String cityName) {
        Set<City> cities = cityMapper.findByCityName(cityName);
        if (cities.isEmpty()) {
            return Set.of();
        }
        return restaurantMapper.findByCity(cities.iterator().next());
    }

    public Set<Restaurant> findByType(RestaurantType type) {
        return restaurantMapper.findByType(type);
    }

    /**
     * Transaction atomique : Restaurant + City (création si nécessaire)
     */
    public Restaurant create(Restaurant restaurant) {
        try {
            // 1. Gestion de la ville
            City city = restaurant.getAddress().getCity();
            if (city.getId() == null) {
                City existingCity = cityMapper.findByZipCode(city.getZipCode());
                if (existingCity != null) {
                    restaurant.getAddress().setCity(existingCity);
                } else {
                    City createdCity = cityMapper.create(city);
                    restaurant.getAddress().setCity(createdCity);
                }
            }

            // 2. Persistance du restaurant (Localisation est @Embeddable, donc pas de persist séparé)
            return restaurantMapper.create(restaurant);
        } catch (Exception e) {
            logger.error("Erreur lors de la création du restaurant", e);
            return null;
        }
    }

    public boolean update(Restaurant restaurant) {
        return restaurantMapper.update(restaurant);
    }

    /**
     * Suppression en cascade : Restaurant → Evaluations (Basic + Complete) → Grades
     */
    public boolean delete(Restaurant restaurant) {
        return restaurantMapper.delete(restaurant);
    }
}