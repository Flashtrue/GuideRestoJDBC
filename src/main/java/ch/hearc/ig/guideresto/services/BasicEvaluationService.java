package ch.hearc.ig.guideresto.services;

import ch.hearc.ig.guideresto.business.*;
import ch.hearc.ig.guideresto.persistence.BasicEvaluationMapper;

import java.util.Date;
import java.util.Set;

public class BasicEvaluationService extends AbstractService {

    private final BasicEvaluationMapper basicEvaluationMapper = new BasicEvaluationMapper();

    public Set<BasicEvaluation> getAll() {
        return basicEvaluationMapper.findAll();
    }

    public BasicEvaluation findById(int id) {
        return basicEvaluationMapper.findById(id);
    }

    public Set<BasicEvaluation> findByRestaurant(Restaurant restaurant) {
        return basicEvaluationMapper.findByRestaurant(restaurant);
    }

    public BasicEvaluation create(Restaurant restaurant, boolean like, String ipAddress) {
        try {
            BasicEvaluation evaluation = new BasicEvaluation(null, new Date(), restaurant, like, ipAddress);
            BasicEvaluation created = basicEvaluationMapper.create(evaluation);
            if (created != null) {
                restaurant.getEvaluations().add(created);
            }
            return created;
        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'évaluation basique", e);
            return null;
        }
    }

    public boolean update(BasicEvaluation evaluation) {
        return basicEvaluationMapper.update(evaluation);
    }

    public boolean delete(BasicEvaluation evaluation) {
        return basicEvaluationMapper.delete(evaluation);
    }

    public int countLikes(Restaurant restaurant, boolean likeRestaurant) {
        return (int) findByRestaurant(restaurant).stream()
                .filter(eval -> eval.getLikeRestaurant() == likeRestaurant)
                .count();
    }
}