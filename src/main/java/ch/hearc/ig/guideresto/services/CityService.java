package ch.hearc.ig.guideresto.services;

import ch.hearc.ig.guideresto.business.City;
import ch.hearc.ig.guideresto.persistence.CityMapper;

import java.sql.SQLException;
import java.util.Set;

public class CityService extends AbstractService {

    private final CityMapper cityMapper;

    public CityService() {
        this.cityMapper = new CityMapper();
    }

    public Set<City> getAll() {
        return cityMapper.findAll();
    }

    public City findById(int id) {
        return cityMapper.findById(id);
    }

    public City findByZipCode(String zipCode) {
        Set<City> cities = getAll();

        for (City city : cities) {
            if (city.getZipCode().equals(zipCode)) {
                return city;
            }
        }

        return null;
    }

    public City create(City city) {
        try {
            executeInTransaction(() -> {
                City createdCity = cityMapper.create(city); 
                if (createdCity != null && createdCity.getId() != null) {
                    city.setId(createdCity.getId()); 
                }
            });
            return city;
        } catch (SQLException e) {
            logger.error("Erreur lors de la création de la ville", e);
            return null;
        }
    }

    public boolean update(City city) {
        try {
            executeInTransaction(() -> {
                cityMapper.update(city);
            });
            return true;
        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour de la ville", e);
            return false;
        }
    }

    public boolean delete(City city) {
        try {
            executeInTransaction(() -> {
                cityMapper.delete(city);
            });
            return true;
        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression de la ville", e);
            return false;
        }
    }
}