package ch.hearc.ig.guideresto.services;

import ch.hearc.ig.guideresto.business.*;
import ch.hearc.ig.guideresto.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class CompleteEvaluationService extends AbstractService {

    private final CompleteEvaluationMapper completeEvaluationMapper = new CompleteEvaluationMapper();
    private final GradeMapper gradeMapper = new GradeMapper();
    private final GradeService gradeService = new GradeService();

    public Set<CompleteEvaluation> getAll() {
        return completeEvaluationMapper.findAll();
    }

    public CompleteEvaluation findById(int id) {
        return completeEvaluationMapper.findById(id);
    }

    public Set<CompleteEvaluation> findByRestaurantId(int restaurantId) {
        return completeEvaluationMapper.findByRestaurantId(restaurantId);
    }

    /**
     * Transaction atomique : CompleteEvaluation + tous les Grades
     */
    public CompleteEvaluation create(Restaurant restaurant, String username, String comment, Set<Grade> grades) {
        try {
            // 1. Création de l'évaluation
            CompleteEvaluation evaluation = new CompleteEvaluation(null, new Date(), restaurant, comment, username);
            CompleteEvaluation created = completeEvaluationMapper.create(evaluation);
            
            if (created == null) {
                return null;
            }

            // 2. Création de toutes les notes
            Set<Grade> createdGrades = new HashSet<>();
            for (Grade grade : grades) {
                grade.setEvaluation(created);
                Grade createdGrade = gradeService.createGrade(grade);
                if (createdGrade != null) {
                    createdGrades.add(createdGrade);
                }
            }

            created.setGrades(createdGrades);
            restaurant.getEvaluations().add(created);

            return created;
        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'évaluation complète", e);
            return null;
        }
    }

    public boolean update(CompleteEvaluation evaluation) {
        try {
            executeInTransaction(em -> em.merge(evaluation));
            return true;
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de l'évaluation", e);
            return false;
        }
    }

    public boolean delete(CompleteEvaluation evaluation) {
        try {
            executeInTransaction(em -> {
                CompleteEvaluation managed = em.contains(evaluation) ? evaluation : em.merge(evaluation);

                Set<Grade> grades = gradeMapper.findByEvaluation(managed);
                grades.forEach(grade -> {
                    Grade managedGrade = em.contains(grade) ? grade : em.merge(grade);
                    em.remove(managedGrade);
                });

                em.remove(managed);
            });
            return true;
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de l'évaluation", e);
            return false;
        }
    }

    public Set<CompleteEvaluation> getCompleteEvaluationsWithGrades(Restaurant restaurant) {
        Set<CompleteEvaluation> evaluations = completeEvaluationMapper.findByRestaurant(restaurant);
        evaluations.forEach(eval -> {
            Set<Grade> grades = gradeMapper.findByEvaluation(eval);
            eval.setGrades(grades);
        });
        return evaluations;
    }
}