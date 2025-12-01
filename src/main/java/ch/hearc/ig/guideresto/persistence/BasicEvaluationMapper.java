package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.BasicEvaluation;
import ch.hearc.ig.guideresto.business.Restaurant;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class BasicEvaluationMapper extends AbstractMapper<BasicEvaluation> {
    public BasicEvaluationMapper() {
        super(BasicEvaluation.class);
    }

    @Override
    public Set<BasicEvaluation> findAll() {
        return new LinkedHashSet<>(em()
                .createNamedQuery("BasicEvaluation.findAll", BasicEvaluation.class)
                .getResultList());
    }

    public Set<BasicEvaluation> findByRestaurant(Restaurant restaurant) {
        if (restaurant == null) {
            return Collections.emptySet();
        }
        return new LinkedHashSet<>(em()
                .createNamedQuery("BasicEvaluation.findByRestaurant", BasicEvaluation.class)
                .setParameter("restaurant", restaurant)
                .getResultList());
    }

    public Set<BasicEvaluation> findByLikeRestaurant(Boolean likeRestaurant) {
        if (likeRestaurant == null) {
            return Collections.emptySet();
        }
        return new LinkedHashSet<>(em()
                .createNamedQuery("BasicEvaluation.findByLikeRestaurant", BasicEvaluation.class)
                .setParameter("likeRestaurant", likeRestaurant)
                .getResultList());
    }

    public Set<BasicEvaluation> findByIpAddress(String ipAddress) {
        if (ipAddress == null) {
            return Collections.emptySet();
        }
        return new LinkedHashSet<>(em()
                .createNamedQuery("BasicEvaluation.findByIpAddress", BasicEvaluation.class)
                .setParameter("ipAddress", ipAddress)
                .getResultList());
    }
}