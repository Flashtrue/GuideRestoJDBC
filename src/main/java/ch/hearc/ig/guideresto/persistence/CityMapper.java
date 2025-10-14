package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.City;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class CityMapper extends AbstractMapper<City> {



    @Override
    public City findById(int id) {
        Connection connection = ConnectionUtils.getConnection();
        String query = "SELECT * FROM villes WHERE numero = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCity(rs);
                }
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la recherche de la ville avec l'id: " + id, ex);
        }
        return null;
    };

    @Override
    public Set<City> findAll() {
        Set<City> cities = new HashSet<>();
        Connection connection = ConnectionUtils.getConnection();
        String query = "SELECT * FROM villes";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                cities.add(mapResultSetToCity(rs));
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la récupération de toutes les villes", ex);
        }
        return cities;
    };

    @Override
    public City create(City city) {
        Connection connection = ConnectionUtils.getConnection();
        String query = "INSERT INTO villes (code_postal, nom_ville) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, city.getZipCode());
            stmt.setString(2, city.getCityName());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        city.setId(generatedKeys.getInt(1));
                        return city;
                    }
                }
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la création de la ville: " + city, ex);
        }
        return null;
    };

    @Override
    public boolean update(City city) {
        Connection connection = ConnectionUtils.getConnection();
        String query = "UPDATE villes SET code_postal = ?, nom_ville = ? WHERE numero = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, city.getZipCode());
            stmt.setString(2, city.getCityName());
            stmt.setInt(3, city.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                return true;
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la mise à jour de la ville: " + city, ex);
        }
        return false;
    };

    @Override
    public boolean delete(City city) {
        return deleteById(city.getId());
    };

    @Override
    public boolean deleteById(int id) {
        Connection connection = ConnectionUtils.getConnection();
        String query = "DELETE FROM villes WHERE numero = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException ex) {
            logger.error("Erreur lors de la suppression de la ville avec l'id: " + id, ex);
        }
        return false;
    };

    @Override
    protected String getSequenceQuery() {
        return "SELECT nextval('villes_seq')";
    };

    @Override
    protected String getExistsQuery() {
        return "SELECT 1 FROM villes WHERE numero = ?";
    };

    @Override
    protected String getCountQuery() {
        return "SELECT COUNT(*) FROM villes";
    };

    private City mapResultSetToCity(ResultSet rs) throws SQLException {
        City city = new City();
        city.setId(rs.getInt("numero"));
        city.setZipCode(rs.getString("code_postal"));
        city.setCityName(rs.getString("nom_ville"));
        return city;
    };
}
