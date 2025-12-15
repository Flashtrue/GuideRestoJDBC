package ch.hearc.ig.guideresto.services;

import ch.hearc.ig.guideresto.business.*;
import ch.hearc.ig.guideresto.persistence.GradeMapper;

import java.util.Set;

public class GradeService extends AbstractService {

    private final GradeMapper gradeMapper = new GradeMapper();

    public Set<Grade> getAll() {
        return gradeMapper.findAll();
    }

    public Grade findById(int id) {
        return gradeMapper.findById(id);
    }

    public Set<Grade> findByEvaluation(CompleteEvaluation evaluation) {
        return gradeMapper.findByEvaluation(evaluation);
    }

    public Set<Grade> findByEvaluationId(int evaluationId) {
        return gradeMapper.findByEvaluationId(evaluationId);
    }

    public Grade createGrade(Grade grade) {
        return gradeMapper.create(grade);
    }

    public boolean update(Grade grade) {
        try {
            executeInTransaction(em -> em.merge(grade));
            return true;
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de la note", e);
            return false;
        }
    }

    public boolean delete(Grade grade) {
        try {
            executeInTransaction(em -> {
                Grade managed = em.contains(grade) ? grade : em.merge(grade);
                em.remove(managed);
            });
            return true;
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de la note", e);
            return false;
        }
    }
}