package ch.hearc.ig.guideresto.services;

import ch.hearc.ig.guideresto.business.*;
import ch.hearc.ig.guideresto.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Service gérant les opérations sur les évaluations complètes avec commentaires et notes.
 */
public class CompleteEvaluationService extends AbstractService {

    private final CompleteEvaluationMapper completeEvaluationMapper = new CompleteEvaluationMapper();
    private final GradeMapper gradeMapper = new GradeMapper();
    private final GradeService gradeService = new GradeService();

    /**
     * Récupère toutes les évaluations complètes.
     *
     * @return l'ensemble des évaluations complètes
     */
    public Set<CompleteEvaluation> getAll() {
        return completeEvaluationMapper.findAll();
    }

    /**
     * Recherche une évaluation complète par son identifiant.
     *
     * @param id l'identifiant de l'évaluation
     * @return l'évaluation trouvée ou null si non trouvée
     */
    public CompleteEvaluation findById(int id) {
        return completeEvaluationMapper.findById(id);
    }

    /**
     * Récupère toutes les évaluations complètes pour un restaurant donné.
     *
     * @param restaurantId l'identifiant du restaurant
     * @return l'ensemble des évaluations du restaurant
     */
    public Set<CompleteEvaluation> findByRestaurantId(int restaurantId) {
        return completeEvaluationMapper.findByRestaurantId(restaurantId);
    }

    /**
     * Crée une nouvelle évaluation complète avec ses notes associées de manière atomique.
     *
     * @param restaurant le restaurant évalué
     * @param username le nom de l'utilisateur
     * @param comment le commentaire de l'évaluation
     * @param grades l'ensemble des notes à associer
     * @return l'évaluation créée ou null en cas d'erreur
     */
    public CompleteEvaluation create(Restaurant restaurant, String username, String comment, Set<Grade> grades) {
        return executeInTransactionWithResult(em -> {
            try {
                // 1. Création de l'évaluation
                CompleteEvaluation evaluation = new CompleteEvaluation(null, new Date(), restaurant, comment, username);
                CompleteEvaluation created = completeEvaluationMapper.create(evaluation);

                if (created == null) {
                    throw new RuntimeException("Impossible de créer l'évaluation");
                }

                // 2. Création de toutes les notes
                Set<Grade> createdGrades = new HashSet<>();
                for (Grade grade : grades) {
                    grade.setEvaluation(created);
                    Grade createdGrade = gradeMapper.create(grade);
                    if (createdGrade == null) {
                        throw new RuntimeException("Impossible de créer la note");
                    }
                    createdGrades.add(createdGrade);
                }

                created.setGrades(createdGrades);
                restaurant.getEvaluations().add(created);

                return created;
            } catch (Exception e) {
                logger.error("Erreur lors de la création de l'évaluation complète", e);
                throw e; // Relancer pour rollback automatique
            }
        });
    }

    /**
     * Met à jour une évaluation complète existante.
     *
     * @param evaluation l'évaluation à mettre à jour
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean update(CompleteEvaluation evaluation) {
        return executeInTransactionWithResult(em -> completeEvaluationMapper.update(evaluation));
    }

    /**
     * Supprime une évaluation complète.
     *
     * @param evaluation l'évaluation à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean delete(CompleteEvaluation evaluation) {
        return executeInTransactionWithResult(em -> completeEvaluationMapper.delete(evaluation));
    }

    /**
     * Récupère toutes les évaluations complètes d'un restaurant avec leurs notes chargées.
     *
     * @param restaurant le restaurant concerné
     * @return l'ensemble des évaluations avec leurs notes
     */
    public Set<CompleteEvaluation> getCompleteEvaluationsWithGrades(Restaurant restaurant) {
        Set<CompleteEvaluation> evaluations = completeEvaluationMapper.findByRestaurant(restaurant);
        evaluations.forEach(eval -> {
            Set<Grade> grades = gradeMapper.findByEvaluation(eval);
            eval.setGrades(grades);
        });
        return evaluations;
    }
}