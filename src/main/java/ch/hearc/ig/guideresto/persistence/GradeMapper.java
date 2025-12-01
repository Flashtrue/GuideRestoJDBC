package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.Grade;
import ch.hearc.ig.guideresto.business.CompleteEvaluation;
import ch.hearc.ig.guideresto.business.EvaluationCriteria;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class GradeMapper extends AbstractMapper<Grade> {

    public GradeMapper() {
        super(Grade.class);
    }

    @Override
    public Set<Grade> findAll() {
        return new LinkedHashSet<>(em()
                .createNamedQuery("Grade.findAll", Grade.class)
                .getResultList());
    }

    public Set<Grade> findByEvaluation(CompleteEvaluation evaluation) {
        if (evaluation == null) {
            return Collections.emptySet();
        }
        return new LinkedHashSet<>(em()
                .createNamedQuery("Grade.findByEvaluation", Grade.class)
                .setParameter("evaluation", evaluation)
                .getResultList());
    }

    public Set<Grade> findByCriteria(EvaluationCriteria criteria) {
        if (criteria == null) {
            return Collections.emptySet();
        }
        return new LinkedHashSet<>(em()
                .createNamedQuery("Grade.findByCriteria", Grade.class)
                .setParameter("criteria", criteria)
                .getResultList());
    }

    public Set<Grade> findByGradeValue(Integer gradeValue) {
        if (gradeValue == null) {
            return Collections.emptySet();
        }
        return new LinkedHashSet<>(em()
                .createNamedQuery("Grade.findByGradeValue", Grade.class)
                .setParameter("grade", gradeValue)
                .getResultList());
    }

    public Set<Grade> findByEvaluationId(int evaluationId) {
        if (evaluationId <= 0) {
            return Collections.emptySet();
        }
        return new LinkedHashSet<>(em()
                .createQuery("SELECT g FROM Grade g WHERE g.evaluation.id = :evaluationId", Grade.class)
                .setParameter("evaluationId", evaluationId)
                .getResultList());
    }
}
