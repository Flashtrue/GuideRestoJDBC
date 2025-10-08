package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.CompleteEvaluation;
import ch.hearc.ig.guideresto.business.Restaurant;
import ch.hearc.ig.guideresto.business.Grade;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class CompleteEvaluationMapper extends AbstractMapper<CompleteEvaluation> {

    @Override
    public CompleteEvaluation findById(int id) {
        Connection connection = ConnectionUtils.getConnection();
        String query = "SELECT * FROM commentaires WHERE numero = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCompleteEvaluation(rs);
                }
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la recherche de l'évaluation complète avec l'id: " + id, ex);
        }
        return null;
    }

    @Override
    public Set<CompleteEvaluation> findAll() {
        Set<CompleteEvaluation> evaluations = new HashSet<>();
        Connection connection = ConnectionUtils.getConnection();
        String query = "SELECT * FROM commentaires";
        
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                evaluations.add(mapResultSetToCompleteEvaluation(rs));
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la récupération de toutes les évaluations complètes", ex);
        }
        return evaluations;
    }

    @Override
    public CompleteEvaluation create(CompleteEvaluation evaluation) {
        Connection connection = ConnectionUtils.getConnection();
        String query = "INSERT INTO commentaires (date_eval, commentaire, nom_utilisateur, fk_rest) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, new java.sql.Date(evaluation.getVisitDate().getTime()));
            stmt.setString(2, evaluation.getComment());
            stmt.setString(3, evaluation.getUsername());
            stmt.setInt(4, evaluation.getRestaurant().getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        evaluation.setId(generatedKeys.getInt(1));
                        addToCache(evaluation);
                        return evaluation;
                    }
                }
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la création de l'évaluation complète", ex);
        }
        return null;
    }

    @Override
    public boolean update(CompleteEvaluation evaluation) {
        Connection connection = ConnectionUtils.getConnection();
        String query = "UPDATE commentaires SET date_eval = ?, commentaire = ?, nom_utilisateur = ?, fk_rest = ? WHERE numero = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDate(1, new java.sql.Date(evaluation.getVisitDate().getTime()));
            stmt.setString(2, evaluation.getComment());
            stmt.setString(3, evaluation.getUsername());
            stmt.setInt(4, evaluation.getRestaurant().getId());
            stmt.setInt(5, evaluation.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                addToCache(evaluation);
                return true;
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la mise à jour de l'évaluation complète avec l'id: " + evaluation.getId(), ex);
        }
        return false;
    }

    @Override
    public boolean delete(CompleteEvaluation evaluation) {
        return deleteById(evaluation.getId());
    }

    @Override
    public boolean deleteById(int id) {
        Connection connection = ConnectionUtils.getConnection();
        String query = "DELETE FROM commentaires WHERE numero = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                removeFromCache(id);
                return true;
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la suppression de l'évaluation complète avec l'id: " + id, ex);
        }
        return false;
    }

    @Override
    protected String getSequenceQuery() {
        return "SELECT nextval('commentaires_numero_seq')";
    }

    @Override
    protected String getExistsQuery() {
        return "SELECT 1 FROM commentaires WHERE numero = ?";
    }

    @Override
    protected String getCountQuery() {
        return "SELECT COUNT(*) FROM commentaires";
    }

    // Méthode privée pour mapper ResultSet vers CompleteEvaluation
    private CompleteEvaluation mapResultSetToCompleteEvaluation(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("numero");
        java.util.Date visitDate = rs.getDate("date_eval");
        String comment = rs.getString("commentaire");
        String username = rs.getString("nom_utilisateur");
        
        // TODO: Récupérer le restaurant associé (via RestaurantMapper ou requête JOIN)
        // Vous devrez implémenter cette partie selon votre RestaurantMapper
        Restaurant restaurant = null; // À implémenter selon votre logique
        
        CompleteEvaluation evaluation = new CompleteEvaluation(id, visitDate, restaurant, comment, username);
        
        // TODO: Charger les grades associés si nécessaire
        // evaluation.setGrades(loadGradesForEvaluation(id));
        
        return evaluation;
    }

    // Méthodes utilitaires supplémentaires
    public Set<CompleteEvaluation> findByRestaurant(Restaurant restaurant) {
        Set<CompleteEvaluation> evaluations = new HashSet<>();
        Connection connection = ConnectionUtils.getConnection();
        String query = "SELECT * FROM commentaires WHERE fk_rest = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, restaurant.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    evaluations.add(mapResultSetToCompleteEvaluation(rs));
                }
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la recherche des évaluations pour le restaurant: " + restaurant.getId(), ex);
        }
        return evaluations;
    }
}