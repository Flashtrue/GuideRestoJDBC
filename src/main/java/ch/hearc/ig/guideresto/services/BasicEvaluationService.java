package ch.hearc.ig.guideresto.services;

import ch.hearc.ig.guideresto.business.BasicEvaluation;
import ch.hearc.ig.guideresto.business.Restaurant;
import ch.hearc.ig.guideresto.persistence.BasicEvaluationMapper;

import java.sql.SQLException;
import java.util.Date;
import java.util.Set;

public class BasicEvaluationService extends AbstractService {

    private final BasicEvaluationMapper basicEvaluationMapper;

    public BasicEvaluationService() {
        this.basicEvaluationMapper = new BasicEvaluationMapper();
    }

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
            executeInTransaction(() -> {
                basicEvaluationMapper.create(evaluation);
            });

            // Mettre à jour la collection d'évaluations du restaurant en mémoire
            restaurant.getEvaluations().add(evaluation);

            return evaluation;
        } catch (SQLException e) {
            logger.error("Erreur lors de l'ajout d'une évaluation basique", e);
            return null;
        }
    }

    public boolean update(BasicEvaluation evaluation) {
        try {
            executeInTransaction(() -> {
                basicEvaluationMapper.update(evaluation);
            });
            return true;
        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour de l'évaluation basique", e);
            return false;
        }
    }

    public boolean delete(BasicEvaluation evaluation) {
        try {
            executeInTransaction(() -> {
                basicEvaluationMapper.delete(evaluation);
            });
            return true;
        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression de l'évaluation basique", e);
            return false;
        }
    }

    public int countLikes(Restaurant restaurant, boolean likeRestaurant) {
        int count = 0;
        Set<BasicEvaluation> evaluations = findByRestaurant(restaurant);

        for (BasicEvaluation evaluation : evaluations) {
            if (evaluation.getLikeRestaurant() == likeRestaurant) {
                count++;
            }
        }

        return count;
    }
}