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
    //Takes the data and creates a java object
    protected Restaurant mapResultSetToRestaurant(ResultSet rs) throws SQLException {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(rs.getInt("NUMERO"));
        restaurant.setName(rs.getString("NOM"));
        restaurant.setDescription(rs.getString("DESCRIPTION"));
        restaurant.setWebsite(rs.getString("SITE_WEB"));

        // Relations
        int cityId = rs.getInt("FK_VILL");
        City city = cityMapper.findById(cityId);

        int typeId = rs.getInt("FK_TYPE");
        RestaurantType type = restaurantTypeMapper.findById(typeId);

        String street = rs.getString("ADRESSE");
        Localisation address = new Localisation(street, city);

        restaurant.setAddress(address);
        restaurant.setType(type);

        return restaurant;
    }

    @Override
    public Restaurant findById(int id) {
        String sql = "SELECT * FROM RESTAURANT WHERE NUMERO = ?";
        Connection connextion = ConnectionUtils.getConnection();

        try (PreparedStatement stmt = connextion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRestaurant(rs);
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
                restaurants.add(mapResultSetToRestaurant(rs));
            }
        } catch (SQLException ex) {
            logger.error("Erreur lors de la recherche de tous les restaurants", ex);
        }
        return restaurants;
    }

    @Override
    public Restaurant create(Restaurant restaurant) {
        String sql = "INSERT INTO RESTAURANTS (NUMERO, NOM, DESCRIPTION, SITE_WEB, FK_VILLE, FK_TYPE) " +
                "VALUES (SEQ_RESTAURANTS.NEXTVAL, ?, ?, ?, ?, ?)";
        Connection connection = ConnectionUtils.getConnection();

        try (PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"NUMERO"})) {
            stmt.setString(1, restaurant.getName());
            stmt.setString(2, restaurant.getDescription());
            stmt.setString(3, restaurant.getWebsite());

            if (restaurant.getAddress() != null) {
                stmt.setInt(4, restaurant.getAddress().getCity().getId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            if (restaurant.getType() != null) {
                stmt.setInt(5, restaurant.getType().getId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        restaurant.setId(generatedKeys.getInt(1));
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
        String sql = "UPDATE RESTAURANTS SET NOM = ?, DESCRIPTION = ?, SITE_WEB = ?, FK_VILLE = ?, FK_TYPE = ? " +
                "WHERE NUMERO = ?";
        Connection connection = ConnectionUtils.getConnection();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, restaurant.getName());
            stmt.setString(2, restaurant.getDescription());
            stmt.setString(3, restaurant.getWebsite());

            if (restaurant.getAddress() != null) {
                stmt.setInt(4, restaurant.getAddress().getCity().getId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            if (restaurant.getType() != null) {
                stmt.setInt(5, restaurant.getType().getId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            stmt.setInt(6, restaurant.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            logger.error("Erreur lors de la mise à jour du restaurant ID: {}", restaurant.getId(), ex);
        }
        return false;
    }

    @Override
    public boolean delete(Restaurant restaurant) {
        return deleteById(restaurant.getId());
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM RESTAURANTS WHERE NUMERO = ?";
        Connection connection = ConnectionUtils.getConnection();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            logger.error("Erreur lors de la suppression du restaurant ID: {}", id, ex);
        }
        return false;
    }

    @Override
    protected String getSequenceQuery() {
        return "SELECT SEQ_RESTAURANTS.CURRVAL FROM DUAL";
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








