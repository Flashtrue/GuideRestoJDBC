package ch.hearc.ig.guideresto.services;

import ch.hearc.ig.guideresto.business.EvaluationCriteria;
import ch.hearc.ig.guideresto.persistence.EvaluationCriteriaMapper;

import java.util.Set;

public class EvaluationCriteriaService extends AbstractService {

    private final EvaluationCriteriaMapper evaluationCriteriaMapper = new EvaluationCriteriaMapper();

    public Set<EvaluationCriteria> getAll() {
        return evaluationCriteriaMapper.findAll();
    }

    public EvaluationCriteria findById(int id) {
        return evaluationCriteriaMapper.findById(id);
    }

    public EvaluationCriteria create(EvaluationCriteria criteria) {
        try {
            return executeInTransactionWithResult(em -> {
                em.persist(criteria);
                return criteria;
            });
        } catch (Exception e) {
            logger.error("Erreur lors de la création du critère d'évaluation", e);
            return null;
        }
    }

    public boolean update(EvaluationCriteria criteria) {
        try {
            executeInTransaction(em -> em.merge(criteria));
            return true;
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du critère d'évaluation", e);
            return false;
        }
    }

    public boolean delete(EvaluationCriteria criteria) {
        try {
            executeInTransaction(em -> {
                EvaluationCriteria managed = em.contains(criteria) ? criteria : em.merge(criteria);
                em.remove(managed);
            });
            return true;
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression du critère d'évaluation", e);
            return false;
        }
    }
}