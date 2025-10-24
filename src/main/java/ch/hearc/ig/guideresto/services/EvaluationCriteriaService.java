package ch.hearc.ig.guideresto.services;

import ch.hearc.ig.guideresto.business.EvaluationCriteria;
import ch.hearc.ig.guideresto.persistence.EvaluationCriteriaMapper;

import java.sql.SQLException;
import java.util.Set;

public class EvaluationCriteriaService extends AbstractService {

    private final EvaluationCriteriaMapper evaluationCriteriaMapper;

    public EvaluationCriteriaService() {
        this.evaluationCriteriaMapper = new EvaluationCriteriaMapper();
    }

    public Set<EvaluationCriteria> getAll() {
        return evaluationCriteriaMapper.findAll();
    }

    public EvaluationCriteria findById(int id) {
        return evaluationCriteriaMapper.findById(id);
    }

    public EvaluationCriteria create(EvaluationCriteria criteria) {
        try {
            executeInTransaction(() -> {
                EvaluationCriteria created = evaluationCriteriaMapper.create(criteria);
                if (created == null) {
                    throw new RuntimeException("Échec de la création du critère d'évaluation");
                }
            });
            return criteria;
        } catch (SQLException e) {
            logger.error("Erreur lors de la création du critère d'évaluation", e);
            return null;
        }
    }

    public boolean update(EvaluationCriteria criteria) {
        try {
            executeInTransaction(() -> {
                boolean updated = evaluationCriteriaMapper.update(criteria);
                if (!updated) {
                    throw new RuntimeException("Échec de la mise à jour du critère d'évaluation");
                }
            });
            return true;
        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour du critère d'évaluation", e);
            return false;
        }
    }

    public boolean delete(EvaluationCriteria criteria) {
        try {
            executeInTransaction(() -> {
                boolean deleted = evaluationCriteriaMapper.delete(criteria);
                if (!deleted) {
                    throw new RuntimeException("Échec de la suppression du critère d'évaluation");
                }
            });
            return true;
        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression du critère d'évaluation", e);
            return false;
        }
    }
}