package ch.hearc.ig.guideresto.services;

import ch.hearc.ig.guideresto.business.EvaluationCriteria;
import ch.hearc.ig.guideresto.persistence.EvaluationCriteriaMapper;

import java.util.Set;

/**
 * Service gérant les opérations sur les critères d'évaluation.
 */
public class EvaluationCriteriaService extends AbstractService {

    private final EvaluationCriteriaMapper evaluationCriteriaMapper = new EvaluationCriteriaMapper();

    /**
     * Récupère tous les critères d'évaluation.
     * 
     * @return l'ensemble des critères d'évaluation
     */
    public Set<EvaluationCriteria> getAll() {
        return evaluationCriteriaMapper.findAll();
    }

    /**
     * Recherche un critère d'évaluation par son identifiant.
     * 
     * @param id l'identifiant du critère
     * @return le critère trouvé ou null si non trouvé
     */
    public EvaluationCriteria findById(int id) {
        return evaluationCriteriaMapper.findById(id);
    }

    /**
     * Crée un nouveau critère d'évaluation.
     * 
     * @param criteria le critère à créer
     * @return le critère créé ou null en cas d'erreur
     */
    public EvaluationCriteria create(EvaluationCriteria criteria) {
        return evaluationCriteriaMapper.create(criteria);
    }

    /**
     * Met à jour un critère d'évaluation existant.
     * 
     * @param criteria le critère à mettre à jour
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean update(EvaluationCriteria criteria) {
        return evaluationCriteriaMapper.update(criteria);
    }

    /**
     * Supprime un critère d'évaluation.
     * 
     * @param criteria le critère à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean delete(EvaluationCriteria criteria) {
        return evaluationCriteriaMapper.delete(criteria);
    }
}