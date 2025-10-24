package ch.hearc.ig.guideresto.services;

import ch.hearc.ig.guideresto.business.CompleteEvaluation;
import ch.hearc.ig.guideresto.business.Restaurant;
import ch.hearc.ig.guideresto.business.Grade;
import ch.hearc.ig.guideresto.persistence.CompleteEvaluationMapper;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class CompleteEvaluationService extends AbstractService {

    private final CompleteEvaluationMapper completeEvaluationMapper;
    private final GradeService gradeService;

    public CompleteEvaluationService() {
        this.completeEvaluationMapper = new CompleteEvaluationMapper();
        this.gradeService = new GradeService();
    }

    public Set<CompleteEvaluation> getAll() {
        return completeEvaluationMapper.findAll();
    }

    public CompleteEvaluation findById(int id) {
        return completeEvaluationMapper.findById(id);
    }

    public Set<CompleteEvaluation> findByRestaurantId(int restaurantId) {
        return completeEvaluationMapper.findByRestaurantId(restaurantId);
    }

    public CompleteEvaluation create(Restaurant restaurant, String username, String comment, Set<Grade> grades) {
        try {
            CompleteEvaluation evaluation = new CompleteEvaluation(null, new Date(), restaurant, comment, username);

            executeInTransaction(() -> {
                CompleteEvaluation createdEvaluation = completeEvaluationMapper.create(evaluation);

                if (createdEvaluation == null) {
                    throw new RuntimeException("Échec de la création de l'évaluation");
                }

                // Ajout des notes
                Set<Grade> createdGrades = new HashSet<>();
                for (Grade grade : grades) {
                    grade.setEvaluation(createdEvaluation);
                    Grade createdGrade = gradeService.createGrade(grade);
                    if (createdGrade != null) {
                        createdGrades.add(createdGrade);
                    } else {
                        throw new RuntimeException("Échec de la création d'une note");
                    }
                }

                createdEvaluation.setGrades(createdGrades);
            });

            // Mettre à jour la collection d'évaluations du restaurant en mémoire
            restaurant.getEvaluations().add(evaluation);

            return evaluation;
        } catch (SQLException e) {
            logger.error("Erreur lors de l'ajout d'une évaluation complète", e);
            return null;
        }
    }

    public boolean update(CompleteEvaluation evaluation) {
        try {
            executeInTransaction(() -> {
                completeEvaluationMapper.update(evaluation);
            });
            return true;
        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour de l'évaluation complète", e);
            return false;
        }
    }

    public boolean delete(CompleteEvaluation evaluation) {
        try {
            executeInTransaction(() -> {
                completeEvaluationMapper.delete(evaluation);
            });
            return true;
        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression de l'évaluation complète", e);
            return false;
        }
    }

    public Set<CompleteEvaluation> getCompleteEvaluationsWithGrades(Restaurant restaurant) {
        Set<CompleteEvaluation> evaluations = findByRestaurantId(restaurant.getId());

        // Charger les notes pour chaque évaluation
        for (CompleteEvaluation evaluation : evaluations) {
            Set<Grade> grades = gradeService.findByEvaluationId(evaluation.getId());
            evaluation.setGrades(grades);
        }

        return evaluations;
    }
}