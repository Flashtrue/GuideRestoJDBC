package ch.hearc.ig.guideresto.services;

import ch.hearc.ig.guideresto.business.City;
import ch.hearc.ig.guideresto.persistence.CityMapper;

import java.util.Set;

public class CityService extends AbstractService {

    private final CityMapper cityMapper = new CityMapper();

    public Set<City> getAll() {
        return cityMapper.findAll();
    }

    public City findById(int id) {
        return cityMapper.findById(id);
    }

    public City findByZipCode(String zipCode) {
        return cityMapper.findByZipCode(zipCode);
    }

    public Set<City> findByCityName(String cityName) {
        return cityMapper.findByCityName(cityName);
    }

    public City create(City city) {
        return cityMapper.create(city);
    }

    public boolean update(City city) {
        try {
            executeInTransaction(em -> em.merge(city));
            return true;
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de la ville", e);
            return false;
        }
    }

    public boolean delete(City city) {
        try {
            executeInTransaction(em -> {
                City managed = em.contains(city) ? city : em.merge(city);
                em.remove(managed);
            });
            return true;
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de la ville", e);
            return false;
        }
    }
}