package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.CompleteEvaluation;
import ch.hearc.ig.guideresto.business.Restaurant;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class CompleteEvaluationMapper extends AbstractMapper<CompleteEvaluation> {

    public CompleteEvaluationMapper() {
        super(CompleteEvaluation.class);
    }

    @Override
    public Set<CompleteEvaluation> findAll() {
        return new LinkedHashSet<>(em()
                .createNamedQuery("CompleteEvaluation.findAll", CompleteEvaluation.class)
                .getResultList());
    }

    public Set<CompleteEvaluation> findByRestaurant(Restaurant restaurant) {
        if (restaurant == null) {
            return Collections.emptySet();
        }
        return new LinkedHashSet<>(em()
                .createNamedQuery("CompleteEvaluation.findByRestaurant", CompleteEvaluation.class)
                .setParameter("restaurant", restaurant)
                .getResultList());
    }

    public Set<CompleteEvaluation> findByUsername(String username) {
        if (username == null) {
            return Collections.emptySet();
        }
        String pattern = "%" + username.trim() + "%";
        return new LinkedHashSet<>(em()
                .createNamedQuery("CompleteEvaluation.findByUsername", CompleteEvaluation.class)
                .setParameter("username", pattern)
                .getResultList());
    }

    public Set<CompleteEvaluation> findByRestaurantId(int restaurantId) {
        if (restaurantId <= 0) {
            return Collections.emptySet();
        }
        return new LinkedHashSet<>(em()
                .createQuery("SELECT c FROM CompleteEvaluation c WHERE c.restaurant.id = :restaurantId", CompleteEvaluation.class)
                .setParameter("restaurantId", restaurantId)
                .getResultList());
    }
}