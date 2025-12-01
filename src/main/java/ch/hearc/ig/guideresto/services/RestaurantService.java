package ch.hearc.ig.guideresto.services;

import ch.hearc.ig.guideresto.business.Restaurant;
import ch.hearc.ig.guideresto.business.RestaurantType;
import ch.hearc.ig.guideresto.business.CompleteEvaluation;
import ch.hearc.ig.guideresto.business.BasicEvaluation;
import ch.hearc.ig.guideresto.business.Grade;
import ch.hearc.ig.guideresto.persistence.RestaurantMapper;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class RestaurantService extends AbstractService {

    private final RestaurantMapper restaurantMapper;
    private final BasicEvaluationService basicEvaluationService;
    private final CompleteEvaluationService completeEvaluationService;
    private final GradeService gradeService;

    public RestaurantService() {
        this.restaurantMapper = new RestaurantMapper();
        this.basicEvaluationService = new BasicEvaluationService();
        this.completeEvaluationService = new CompleteEvaluationService();
        this.gradeService = new GradeService();
    }

    public Set<Restaurant> getAllRestaurants() {
        return restaurantMapper.findAll();
    }

    public Restaurant findById(int id) {
        return restaurantMapper.findById(id);
    }

    public Set<Restaurant> findByName(String name) {
        Set<Restaurant> allRestaurants = getAllRestaurants();
        Set<Restaurant> filteredRestaurants = new HashSet<>();

        for (Restaurant restaurant : allRestaurants) {
            if (restaurant.getName().toUpperCase().contains(name.toUpperCase())) {
                filteredRestaurants.add(restaurant);
            }
        }

        return filteredRestaurants;
    }

    public Set<Restaurant> findByCity(String cityName) {
        Set<Restaurant> allRestaurants = getAllRestaurants();
        Set<Restaurant> filteredRestaurants = new HashSet<>();

        for (Restaurant restaurant : allRestaurants) {
            if (restaurant.getAddress().getCity().getCityName().toUpperCase().contains(cityName.toUpperCase())) {
                filteredRestaurants.add(restaurant);
            }
        }

        return filteredRestaurants;
    }

    public Set<Restaurant> findByType(RestaurantType type) {
        Set<Restaurant> allRestaurants = getAllRestaurants();
        Set<Restaurant> filteredRestaurants = new HashSet<>();

        for (Restaurant restaurant : allRestaurants) {
            if (restaurant.getType().getId().equals(type.getId())) {
                filteredRestaurants.add(restaurant);
            }
        }

        return filteredRestaurants;
    }

    public Restaurant create(Restaurant restaurant) {
        try {
            executeInTransaction(() -> {
                Restaurant createdRestaurant = restaurantMapper.create(restaurant);
                if (createdRestaurant != null && createdRestaurant.getId() != null) {
                    restaurant.setId(createdRestaurant.getId());
                }
            });
            return restaurant;
        } catch (SQLException e) {
            logger.error("Erreur lors de la création du restaurant", e);
            return null;
        }
    }

    public boolean update(Restaurant restaurant) {
        try {
            executeInTransaction(() -> {
                restaurantMapper.update(restaurant);
            });
            return true;
        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour du restaurant", e);
            return false;
        }
    }

    public boolean delete(Restaurant restaurant) {
        try {
            executeInTransaction(() -> {
                Set<BasicEvaluation> basicEvaluations = basicEvaluationService.findByRestaurant(restaurant);
                for (BasicEvaluation basicEval : basicEvaluations) {
                    basicEvaluationService.delete(basicEval);
                }

                Set<CompleteEvaluation> completeEvaluations = completeEvaluationService.findByRestaurantId(restaurant.getId());
                for (CompleteEvaluation completeEval : completeEvaluations) {
                    Set<Grade> grades = gradeService.findByEvaluationId(completeEval.getId());
                    
                    for (Grade grade : grades) {
                        gradeService.delete(grade);
                    }
                    
                    completeEvaluationService.delete(completeEval);
                }

                restaurantMapper.delete(restaurant);
            });
            return true;
        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression du restaurant", e);
            return false;
        }
    }
}