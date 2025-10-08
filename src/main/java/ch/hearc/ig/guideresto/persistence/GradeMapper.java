package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.Grade;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class GradeMapper extends AbstractMapper<Grade> {

    @Override
    public Grade findById(int id) {
        Connection connection = ConnectionUtils.getConnection();
        String query = "SELECT * FROM notes WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToGrade(rs);
                }
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la recherche de la note avec l'id: " + id, ex);
        }
        return null;
    };

    @Override
    public Set<Grade> findAll() {
        Set<Grade> grades = new HashSet<>();
        Connection connection = ConnectionUtils.getConnection();
        String query = "SELECT * FROM notes";
        
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                grades.add(mapResultSetToGrade(rs));
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la récupération de toutes les notes", ex);
        }
        return grades;
    };

    @Override
    public Grade create(Grade grade) {
        Connection connection = ConnectionUtils.getConnection();
        String query = "INSERT INTO notes (note, fk_comm, fk_crit) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, grade.getGrade());
            stmt.setInt(2, grade.getEvaluation().getId());
            stmt.setInt(3, grade.getCriteria().getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        grade.setId(generatedKeys.getInt(1));
                        return grade;
                    }
                }
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la création de la note", ex);
        }
        return null;
    };

    @Override
    public boolean update(Grade grade) {
        Connection connection = ConnectionUtils.getConnection();
        String query = "UPDATE notes SET note = ?, fk_comm = ?, fk_crit = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, grade.getGrade());
            stmt.setInt(2, grade.getEvaluation().getId());
            stmt.setInt(3, grade.getCriteria().getId());
            stmt.setInt(4, grade.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException ex) {
            logger.error("Erreur lors de la mise à jour de la note avec l'id: " + grade.getId(), ex);
        }
        return false;
    };

    @Override
    public boolean delete(Grade grade) {
        return deleteById(grade.getId());
    };

    @Override
    public boolean deleteById(int id) {
        Connection connection = ConnectionUtils.getConnection();
        String query = "DELETE FROM notes WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException ex) {
            logger.error("Erreur lors de la suppression de la note avec l'id: " + id, ex);
        }
        return false;
    };

    @Override
    protected String getSequenceQuery() {
        return "SELECT nextval('grade_seq')";
    };
    
    @Override
    protected String getExistsQuery() {
        return "SELECT 1 FROM notes WHERE id = ?";
    };
    
    @Override
    protected String getCountQuery() {
        return "SELECT COUNT(*) FROM notes";
    };

    private Grade mapResultSetToGrade(ResultSet rs) throws SQLException {
        Grade grade = new Grade();
        grade.setId(rs.getInt("id"));
        grade.setGrade(rs.getInt("note"));
        
        CompleteEvaluationMapper evaluationMapper = new CompleteEvaluationMapper();
        grade.setEvaluation(evaluationMapper.findById(rs.getInt("fk_comm")));
        
        EvaluationCriteriaMapper criteriaMapper = new EvaluationCriteriaMapper();
        grade.setCriteria(criteriaMapper.findById(rs.getInt("fk_crit")));

        return grade;
    }; 
}
