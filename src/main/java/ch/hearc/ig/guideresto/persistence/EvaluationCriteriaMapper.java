package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.EvaluationCriteria;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class EvaluationCriteriaMapper extends AbstractMapper<EvaluationCriteria> {

    @Override
    public EvaluationCriteria findById(int id) {
        // Vérifier le cache d'abord
        EvaluationCriteria cachedCriteria = getFromCache(id);
        if (cachedCriteria != null) {
            return cachedCriteria;
        }
        
        Connection connection = ConnectionUtils.getConnection();
        String query = "SELECT * FROM criteres_evaluation WHERE numero = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    EvaluationCriteria criteria = mapResultSetToEvaluationCriteria(rs);
                    addToCache(criteria);
                    return criteria;
                }
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la recherche du critère avec l'id: " + id, ex);
        }
        return null;
    }

    @Override
    public Set<EvaluationCriteria> findAll() {
        Set<EvaluationCriteria> criteriaSet = new HashSet<>();
        Connection connection = ConnectionUtils.getConnection();
        String query = "SELECT * FROM criteres_evaluation";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                EvaluationCriteria criteria = mapResultSetToEvaluationCriteria(rs);
                addToCache(criteria);
                criteriaSet.add(criteria);
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la récupération de tous les critères d'évaluation", ex);
        }
        return criteriaSet;
    }

    @Override
    public EvaluationCriteria create(EvaluationCriteria criteria) {
        Connection connection = ConnectionUtils.getConnection();
        String query = "INSERT INTO criteres_evaluation (nom, description) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, criteria.getName());
            stmt.setString(2, criteria.getDescription());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        criteria.setId(generatedKeys.getInt(1));
                        addToCache(criteria);
                        return criteria;
                    }
                }
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la création du critère d'évaluation", ex);
        }
        return null;
    }

    @Override
    public boolean update(EvaluationCriteria criteria) {
        Connection connection = ConnectionUtils.getConnection();
        String query = "UPDATE criteres_evaluation SET nom = ?, description = ? WHERE numero = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, criteria.getName());
            stmt.setString(2, criteria.getDescription());
            stmt.setInt(3, criteria.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                addToCache(criteria);
                return true;
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la mise à jour du critère d'évaluation avec l'id: " + criteria.getId(), ex);
        }
        return false;
    }

    @Override
    public boolean delete(EvaluationCriteria criteria) {
        return deleteById(criteria.getId());
    }

    @Override
    public boolean deleteById(int id) {
        Connection connection = ConnectionUtils.getConnection();
        String query = "DELETE FROM criteres_evaluation WHERE numero = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                removeFromCache(id);
                return true;
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la suppression du critère d'évaluation avec l'id: " + id, ex);
        }
        return false;
    }
    
    @Override
    protected String getSequenceQuery() {
        return "SELECT seq_criteres_evaluation.currval FROM dual";
    }

    @Override
    protected String getExistsQuery() {
        return "SELECT 1 FROM criteres_evaluation WHERE numero = ?";
    }

    @Override
    protected String getCountQuery() {
        return "SELECT COUNT(*) FROM criteres_evaluation";
    }

    private EvaluationCriteria mapResultSetToEvaluationCriteria(ResultSet rs) throws SQLException {
        EvaluationCriteria criteria = new EvaluationCriteria();
        criteria.setId(rs.getInt("numero"));
        criteria.setName(rs.getString("nom"));
        criteria.setDescription(rs.getString("description"));
        return criteria;
    }
}