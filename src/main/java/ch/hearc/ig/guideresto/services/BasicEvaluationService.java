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
            return executeInTransactionWithResult(em -> {
                BasicEvaluation evaluation = new BasicEvaluation(null, new Date(), restaurant, like, ipAddress);
                em.persist(evaluation);
                restaurant.getEvaluations().add(evaluation);
                return evaluation;
            });
        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'évaluation basique", e);
            return null;
        }
    }

    public boolean update(BasicEvaluation evaluation) {
        try {
            executeInTransaction(em -> em.merge(evaluation));
            return true;
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de l'évaluation", e);
            return false;
        }
    }

    public boolean delete(BasicEvaluation evaluation) {
        try {
            executeInTransaction(em -> {
                BasicEvaluation managed = em.contains(evaluation) ? evaluation : em.merge(evaluation);
                em.remove(managed);
            });
            return true;
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de l'évaluation", e);
            return false;
        }
    }

    public int countLikes(Restaurant restaurant, boolean likeRestaurant) {
        return (int) findByRestaurant(restaurant).stream()
                .filter(eval -> eval.getLikeRestaurant() == likeRestaurant)
                .count();
    }
}