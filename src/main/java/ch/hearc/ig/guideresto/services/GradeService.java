package ch.hearc.ig.guideresto.services;

import ch.hearc.ig.guideresto.business.*;
import ch.hearc.ig.guideresto.persistence.GradeMapper;

import java.util.Set;

/**
 * Service gérant les opérations sur les notes attribuées selon les critères d'évaluation.
 */
public class GradeService extends AbstractService {

    private final GradeMapper gradeMapper = new GradeMapper();

    /**
     * Récupère toutes les notes.
     * 
     * @return l'ensemble des notes
     */
    public Set<Grade> getAll() {
        return gradeMapper.findAll();
    }

    /**
     * Recherche une note par son identifiant.
     * 
     * @param id l'identifiant de la note
     * @return la note trouvée ou null si non trouvée
     */
    public Grade findById(int id) {
        return gradeMapper.findById(id);
    }

    /**
     * Récupère toutes les notes associées à une évaluation complète.
     * 
     * @param evaluation l'évaluation concernée
     * @return l'ensemble des notes de l'évaluation
     */
    public Set<Grade> findByEvaluation(CompleteEvaluation evaluation) {
        return gradeMapper.findByEvaluation(evaluation);
    }

    /**
     * Récupère toutes les notes associées à une évaluation par son identifiant.
     * 
     * @param evaluationId l'identifiant de l'évaluation
     * @return l'ensemble des notes de l'évaluation
     */
    public Set<Grade> findByEvaluationId(int evaluationId) {
        return gradeMapper.findByEvaluationId(evaluationId);
    }

    /**
     * Crée une nouvelle note.
     * 
     * @param grade la note à créer
     * @return la note créée ou null en cas d'erreur
     */
    public Grade createGrade(Grade grade) {
        return gradeMapper.create(grade);
    }

    /**
     * Met à jour une note existante.
     * 
     * @param grade la note à mettre à jour
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean update(Grade grade) {
        return gradeMapper.update(grade);
    }

    /**
     * Supprime une note.
     * 
     * @param grade la note à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean delete(Grade grade) {
        return gradeMapper.delete(grade);
    }
}