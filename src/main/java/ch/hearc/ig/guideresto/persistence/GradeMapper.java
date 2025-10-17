package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.Grade;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class GradeMapper extends AbstractMapper<Grade> {

    @Override
    public Grade findById(int id) {
        Grade cachedGrade = getFromCache(id);
        if (cachedGrade != null) {
            return cachedGrade;
        }
        
        Connection connection = ConnectionUtils.getConnection();
        String query = "SELECT * FROM notes WHERE numero = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Grade grade = mapResultSetToGrade(rs);
                    addToCache(grade);
                    return grade;
                }
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la recherche de la note avec l'id: " + id, ex);
        }
        return null;
    }

    @Override
    public Set<Grade> findAll() {
        Set<Grade> grades = new HashSet<>();
        Connection connection = ConnectionUtils.getConnection();
        String query = "SELECT * FROM notes";
        
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Grade grade = mapResultSetToGrade(rs);
                addToCache(grade);
                grades.add(grade);
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la récupération de toutes les notes", ex);
        }
        return grades;
    }

    @Override
    public Grade create(Grade grade) {
        Connection connection = ConnectionUtils.getConnection();
        String query = "INSERT INTO NOTES (NOTE, FK_COMM, FK_CRIT) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query, new String[]{"NUMERO"})) {
            stmt.setInt(1, grade.getGrade());
            
            if (grade.getEvaluation() == null || grade.getEvaluation().getId() == null) {
                logger.error("L'évaluation associée à cette note est null ou son ID est null");
                return null;
            }
            stmt.setInt(2, grade.getEvaluation().getId());
            
            if (grade.getCriteria() == null || grade.getCriteria().getId() == null) {
                logger.error("Le critère associé à cette note est null ou son ID est null");
                return null;
            }
            stmt.setInt(3, grade.getCriteria().getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        grade.setId(generatedKeys.getInt(1));
                        addToCache(grade);
                        return grade;
                    }
                }
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la création de la note", ex);
        }
        return null;
    }

    @Override
    public boolean update(Grade grade) {
        Connection connection = ConnectionUtils.getConnection();
        String query = "UPDATE notes SET note = ?, fk_comm = ?, fk_crit = ? WHERE numero = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, grade.getGrade());
            stmt.setInt(2, grade.getEvaluation().getId());
            stmt.setInt(3, grade.getCriteria().getId());
            stmt.setInt(4, grade.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                addToCache(grade);
                return true;
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la mise à jour de la note avec l'id: " + grade.getId(), ex);
        }
        return false;
    }

    @Override
    public boolean delete(Grade grade) {
        return deleteById(grade.getId());
    };

    @Override
    public boolean deleteById(int id) {
        Connection connection = ConnectionUtils.getConnection();
        String query = "DELETE FROM notes WHERE numero = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                removeFromCache(id);
                return true;
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la suppression de la note avec l'id: " + id, ex);
        }
        return false;
    }

    @Override
    protected String getSequenceQuery() {
        return "SELECT seq_notes.currval FROM dual";
    }
    
    @Override
    protected String getExistsQuery() {
        return "SELECT 1 FROM notes WHERE numero = ?";
    };
    
    @Override
    protected String getCountQuery() {
        return "SELECT COUNT(*) FROM notes";
    };

    private Grade mapResultSetToGrade(ResultSet rs) throws SQLException {
        Grade grade = new Grade();
        grade.setId(rs.getInt("numero"));
        grade.setGrade(rs.getInt("note"));
        
        CompleteEvaluationMapper evaluationMapper = new CompleteEvaluationMapper();
        grade.setEvaluation(evaluationMapper.findById(rs.getInt("fk_comm")));
        
        EvaluationCriteriaMapper criteriaMapper = new EvaluationCriteriaMapper();
        grade.setCriteria(criteriaMapper.findById(rs.getInt("fk_crit")));

        return grade;
    };

    public Set<Grade> findByEvaluationId(int evaluationId) {
        Set<Grade> grades = new HashSet<>();
        Connection connection = ConnectionUtils.getConnection();
        String query = "SELECT * FROM notes WHERE fk_comm = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, evaluationId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Grade grade = mapResultSetToGrade(rs);
                    addToCache(grade);
                    grades.add(grade);
                }
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la recherche des notes pour l'évaluation ID: " + evaluationId, ex);
        }
        return grades;
    }
}
