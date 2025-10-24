package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.Restaurant;
import ch.hearc.ig.guideresto.business.City;
import ch.hearc.ig.guideresto.business.RestaurantType;
import ch.hearc.ig.guideresto.business.Localisation;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class RestaurantMapper extends AbstractMapper<Restaurant> {
    private final CityMapper cityMapper;
    private final RestaurantTypeMapper restaurantTypeMapper;

    public RestaurantMapper() {
        super();
        this.cityMapper = new CityMapper();
        this.restaurantTypeMapper = new RestaurantTypeMapper();
    }

    @Override
    public Restaurant findById(int id) {
        Restaurant cachedRestaurant = getFromCache(id);
        if (cachedRestaurant != null) {
            return cachedRestaurant;
        }

        String sql = "SELECT * FROM RESTAURANTS WHERE NUMERO = ?";
        Connection connection = ConnectionUtils.getConnection();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Restaurant restaurant = mapResultSetToRestaurant(rs);
                    addToCache(restaurant);
                    return restaurant;
                }
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la recherche du restaurant ID: {}", id, ex);
        }
        return null;
    }

    @Override
    public Set<Restaurant> findAll() {
        Set<Restaurant> restaurants = new HashSet<>();
        String sql = "SELECT * FROM RESTAURANTS";
        Connection connection = ConnectionUtils.getConnection();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Restaurant restaurant = mapResultSetToRestaurant(rs);
                addToCache(restaurant);
                restaurants.add(restaurant);
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la recherche de tous les restaurants", ex);
        }
        return restaurants;
    }

    @Override
    public Restaurant create(Restaurant restaurant) {
        String sql = "INSERT INTO RESTAURANTS (NOM, DESCRIPTION, SITE_WEB, ADRESSE, FK_VILL, FK_TYPE) VALUES (?, ?, ?, ?, ?, ?)";
        Connection connection = ConnectionUtils.getConnection();

        try (PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"NUMERO"})) {
            stmt.setString(1, restaurant.getName());
            stmt.setString(2, restaurant.getDescription());
            stmt.setString(3, restaurant.getWebsite());

            if (restaurant.getAddress() != null) {
                stmt.setString(4, restaurant.getAddress().getStreet());
                stmt.setInt(5, restaurant.getAddress().getCity().getId());
            } else {
                stmt.setNull(4, Types.VARCHAR);
                stmt.setNull(5, Types.INTEGER);
            }

            if (restaurant.getType() != null) {
                stmt.setInt(6, restaurant.getType().getId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        restaurant.setId(generatedKeys.getInt(1));
                        addToCache(restaurant);
                        return restaurant;
                    }
                }
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la création du restaurant", ex);
        }
        return null;
    }

    @Override
    public boolean update(Restaurant restaurant) {
        String sql = "UPDATE RESTAURANTS SET NOM = ?, DESCRIPTION = ?, SITE_WEB = ?, ADRESSE = ?, FK_VILL = ?, FK_TYPE = ? WHERE NUMERO = ?";
        Connection connection = ConnectionUtils.getConnection();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, restaurant.getName());
            stmt.setString(2, restaurant.getDescription());
            stmt.setString(3, restaurant.getWebsite());

            if (restaurant.getAddress() != null) {
                stmt.setString(4, restaurant.getAddress().getStreet());
                stmt.setInt(5, restaurant.getAddress().getCity().getId());
            } else {
                stmt.setNull(4, Types.VARCHAR);
                stmt.setNull(5, Types.INTEGER);
            }

            if (restaurant.getType() != null) {
                stmt.setInt(6, restaurant.getType().getId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            stmt.setInt(7, restaurant.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                addToCache(restaurant);
                return true;
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la mise à jour du restaurant ID: {}", restaurant.getId(), ex);
        }
        return false;
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM RESTAURANTS WHERE NUMERO = ?";
        Connection connection = ConnectionUtils.getConnection();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                removeFromCache(id);
                return true;
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la suppression du restaurant ID: {}", id, ex);
        }
        return false;
    }

    @Override
    public boolean delete(Restaurant restaurant) {
        return deleteById(restaurant.getId());
    }

    protected Restaurant mapResultSetToRestaurant(ResultSet rs) throws SQLException {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(rs.getInt("NUMERO"));
        restaurant.setName(rs.getString("NOM"));
        restaurant.setDescription(rs.getString("DESCRIPTION"));
        restaurant.setWebsite(rs.getString("SITE_WEB"));

        int cityId = rs.getInt("FK_VILL");
        if (!rs.wasNull()) {
            City city = cityMapper.findById(cityId);
            String street = rs.getString("ADRESSE");
            Localisation address = new Localisation(street, city);
            restaurant.setAddress(address);
        }

        int typeId = rs.getInt("FK_TYPE");
        if (!rs.wasNull()) {
            RestaurantType type = restaurantTypeMapper.findById(typeId);
            restaurant.setType(type);
        }

        return restaurant;
    }

    @Override
    protected String getSequenceQuery() {
        return "SELECT restaurants_numero_seq.nextval FROM dual";
    }

    @Override
    protected String getExistsQuery() {
        return "SELECT 1 FROM RESTAURANTS WHERE NUMERO = ?";
    }

    @Override
    protected String getCountQuery() {
        return "SELECT COUNT(*) FROM RESTAURANTS";
    }
}