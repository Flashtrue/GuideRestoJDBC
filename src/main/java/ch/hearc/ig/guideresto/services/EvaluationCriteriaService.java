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
        return evaluationCriteriaMapper.create(criteria);
    }

    public boolean update(EvaluationCriteria criteria) {
        return evaluationCriteriaMapper.update(criteria);
    }

    public boolean delete(EvaluationCriteria criteria) {
        return evaluationCriteriaMapper.delete(criteria);
    }
}