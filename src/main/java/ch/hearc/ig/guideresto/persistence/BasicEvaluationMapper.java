package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.BasicEvaluation;
import ch.hearc.ig.guideresto.business.Restaurant;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class BasicEvaluationMapper extends AbstractMapper<BasicEvaluation> {
    private final RestaurantMapper restaurantMapper;

    public BasicEvaluationMapper() {
        super();
        this.restaurantMapper = new RestaurantMapper();
    }

    @Override
    public BasicEvaluation findById(int id) {
        // Vérifier le cache d'abord
        BasicEvaluation cachedEvaluation = getFromCache(id);
        if (cachedEvaluation != null) {
            return cachedEvaluation;
        }
        
        Connection connection = ConnectionUtils.getConnection();
        String query = "SELECT * FROM LIKES WHERE numero = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BasicEvaluation evaluation = mapResultSetToBasicEvaluation(rs);
                    addToCache(evaluation);
                    return evaluation;
                }
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la recherche de l'appréciation avec l'id: " + id, ex);
        }
        return null;
    }

    @Override
    public Set<BasicEvaluation> findAll() {
        Set<BasicEvaluation> evaluations = new HashSet<>();
        Connection connection = ConnectionUtils.getConnection();
        String query = "SELECT * FROM LIKES";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                BasicEvaluation evaluation = mapResultSetToBasicEvaluation(rs);
                addToCache(evaluation);
                evaluations.add(evaluation);
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la récupération de toutes les appréciations", ex);
        }
        return evaluations;
    }

    @Override
    public BasicEvaluation create(BasicEvaluation evaluation) {
        Connection connection = ConnectionUtils.getConnection();
        String query = "INSERT INTO LIKES (appreciation, date_eval, fk_rest, ip_address) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setBoolean(1, evaluation.getLikeRestaurant());

            if (evaluation.getVisitDate() != null) {
                stmt.setDate(2, new java.sql.Date(evaluation.getVisitDate().getTime()));
            } else {
                stmt.setNull(2, Types.DATE);
            }

            if (evaluation.getRestaurant() != null) {
                stmt.setInt(3, evaluation.getRestaurant().getId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setString(4, evaluation.getIpAddress());

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
            logger.error("Erreur lors de la création de l'appréciation", ex);
        }
        return null;
    }

    @Override
    public boolean update(BasicEvaluation evaluation) {
        Connection connection = ConnectionUtils.getConnection();
        String query = "UPDATE LIKES SET appreciation = ?, date_eval = ?, fk_rest = ?, ip_address = ? WHERE numero = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setBoolean(1, evaluation.getLikeRestaurant());

            if (evaluation.getVisitDate() != null) {
                stmt.setDate(2, new java.sql.Date(evaluation.getVisitDate().getTime()));
            } else {
                stmt.setNull(2, Types.DATE);
            }

            if (evaluation.getRestaurant() != null) {
                stmt.setInt(3, evaluation.getRestaurant().getId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setString(4, evaluation.getIpAddress());
            stmt.setInt(5, evaluation.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                addToCache(evaluation);
                return true;
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la mise à jour de l'appréciation ID: " + evaluation.getId(), ex);
        }
        return false;
    }

    @Override
    public boolean delete(BasicEvaluation evaluation) {
        return deleteById(evaluation.getId());
    }

    @Override
    public boolean deleteById(int id) {
        Connection connection = ConnectionUtils.getConnection();
        String query = "DELETE FROM LIKES WHERE numero = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                removeFromCache(id);
                return true;
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la suppression de l'appréciation ID: " + id, ex);
        }
        return false;
    }

    @Override
    protected String getSequenceQuery() {
        return "SELECT nextval('likes_seq')";
    }

    @Override
    protected String getExistsQuery() {
        return "SELECT 1 FROM LIKES WHERE numero = ?";
    }

    @Override
    protected String getCountQuery() {
        return "SELECT COUNT(*) FROM LIKES";
    }

    private BasicEvaluation mapResultSetToBasicEvaluation(ResultSet rs) throws SQLException {
        BasicEvaluation evaluation = new BasicEvaluation();
        evaluation.setId(rs.getInt("numero"));
        evaluation.setLikeRestaurant(rs.getBoolean("appreciation"));
        evaluation.setVisitDate(rs.getDate("date_eval"));
        evaluation.setIpAddress(rs.getString("ip_address"));

        // Get restaurant using RestaurantMapper
        int restaurantId = rs.getInt("fk_rest");
        if (!rs.wasNull()) {
            Restaurant restaurant = restaurantMapper.findById(restaurantId);
            evaluation.setRestaurant(restaurant);
        }

        return evaluation;
    }

    // Find all BasicEvaluations for a specific Restaurant
    public Set<BasicEvaluation> findByRestaurant(Restaurant restaurant) {
        Set<BasicEvaluation> evaluations = new HashSet<>();
        Connection connection = ConnectionUtils.getConnection();
        String query = "SELECT * FROM LIKES WHERE fk_rest = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, restaurant.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BasicEvaluation evaluation = mapResultSetToBasicEvaluation(rs);
                    addToCache(evaluation);
                    evaluations.add(evaluation);
                }
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la recherche des appréciations pour le restaurant ID: " + restaurant.getId(), ex);
        }
        return evaluations;
    }
}