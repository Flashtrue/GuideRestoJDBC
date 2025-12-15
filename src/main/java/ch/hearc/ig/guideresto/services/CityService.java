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
        return cityMapper.update(city);
    }

    public boolean delete(City city) {
        return cityMapper.delete(city);
    }
}