package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.RestaurantType;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class RestaurantTypeMapper extends AbstractMapper<RestaurantType> {

    @Override
    public RestaurantType findById(int id) {
        // Vérifier d'abord le cache
        RestaurantType cachedType = getFromCache(id);
        if (cachedType != null) {
            return cachedType;
        }

        String sql = "SELECT * FROM TYPES_GASTRONOMIQUES WHERE NUMERO = ?";
        Connection connection = ConnectionUtils.getConnection();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    RestaurantType type = mapResultSetToRestaurantType(rs);
                    addToCache(type);
                    return type;
                }
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la recherche du type de restaurant ID: {}", id, ex);
        }
        return null;
    }

    @Override
    public Set<RestaurantType> findAll() {
        Set<RestaurantType> types = new HashSet<>();
        String sql = "SELECT * FROM TYPES_GASTRONOMIQUES";
        Connection connection = ConnectionUtils.getConnection();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                RestaurantType type = mapResultSetToRestaurantType(rs);
                addToCache(type);
                types.add(type);
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la recherche de tous les types de restaurants", ex);
        }
        return types;
    }

    @Override
    public RestaurantType create(RestaurantType type) {
        String sql = "INSERT INTO TYPES_GASTRONOMIQUES (LIBELLE, DESCRIPTION) VALUES (?, ?)";
        Connection connection = ConnectionUtils.getConnection();

        try (PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"NUMERO"})) {
            stmt.setString(1, type.getLabel());
            stmt.setString(2, type.getDescription());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        type.setId(generatedKeys.getInt(1));
                        addToCache(type);
                        return type;
                    }
                }
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la création du type de restaurant", ex);
        }
        return null;
    }

    @Override
    public boolean update(RestaurantType type) {
        String sql = "UPDATE TYPES_GASTRONOMIQUES SET LIBELLE = ?, DESCRIPTION = ? WHERE NUMERO = ?";
        Connection connection = ConnectionUtils.getConnection();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, type.getLabel());
            stmt.setString(2, type.getDescription());
            stmt.setInt(3, type.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                addToCache(type);
                return true;
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la mise à jour du type de restaurant ID: {}", type.getId(), ex);
        }
        return false;
    }

    @Override
    public boolean delete(RestaurantType type) {
        return deleteById(type.getId());
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM TYPES_GASTRONOMIQUES WHERE NUMERO = ?";
        Connection connection = ConnectionUtils.getConnection();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                removeFromCache(id);
                return true;
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la suppression du type de restaurant ID: {}", id, ex);
        }
        return false;
    }

    protected RestaurantType mapResultSetToRestaurantType(ResultSet rs) throws SQLException {
        RestaurantType type = new RestaurantType();
        type.setId(rs.getInt("NUMERO"));
        type.setLabel(rs.getString("LIBELLE"));
        type.setDescription(rs.getString("DESCRIPTION"));
        return type;
    }

    @Override
    protected String getSequenceQuery() {
        return "SELECT SEQ_TYPES_GASTRONOMIQUES.CURRVAL FROM DUAL";
    }

    @Override
    protected String getExistsQuery() {
        return "SELECT 1 FROM TYPES_GASTRONOMIQUES WHERE NUMERO = ?";
    }

    @Override
    protected String getCountQuery() {
        return "SELECT COUNT(*) FROM TYPES_GASTRONOMIQUES";
    }
}