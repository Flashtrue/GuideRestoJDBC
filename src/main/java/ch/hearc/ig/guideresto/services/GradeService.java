package ch.hearc.ig.guideresto.services;

import ch.hearc.ig.guideresto.business.Grade;
import ch.hearc.ig.guideresto.persistence.GradeMapper;

import java.sql.SQLException;
import java.util.Set;

public class GradeService extends AbstractService {

    private final GradeMapper gradeMapper;

    public GradeService() {
        this.gradeMapper = new GradeMapper();
    }

    public Set<Grade> getAll() {
        return gradeMapper.findAll();
    }

    public Grade findById(int id) {
        return gradeMapper.findById(id);
    }

    public Set<Grade> findByEvaluationId(int evaluationId) {
        return gradeMapper.findByEvaluationId(evaluationId);
    }

    public Grade createGrade(Grade grade) {
        return gradeMapper.create(grade);
    }

    public Grade create(Grade grade) {
        try {
            executeInTransaction(() -> {
                gradeMapper.create(grade);
            });
            return grade;
        } catch (SQLException e) {
            logger.error("Erreur lors de la création de la note", e);
            return null;
        }
    }

    public boolean update(Grade grade) {
        try {
            executeInTransaction(() -> {
                gradeMapper.update(grade);
            });
            return true;
        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour de la note", e);
            return false;
        }
    }

    public boolean delete(Grade grade) {
        try {
            executeInTransaction(() -> {
                gradeMapper.delete(grade);
            });
            return true;
        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression de la note", e);
            return false;
        }
    }
}