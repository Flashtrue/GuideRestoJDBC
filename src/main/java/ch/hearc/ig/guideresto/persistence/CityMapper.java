package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.City;
import jakarta.persistence.TypedQuery;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class CityMapper extends AbstractMapper<City> {

    public CityMapper() {
        super(City.class);
    }

    @Override
    public Set<City> findAll() {
        TypedQuery<City> query = em().createNamedQuery("City.findAll", City.class);
        return new LinkedHashSet<>(query.getResultList());
    }

    public City findByZipCode(String zipCode) {
        if (zipCode == null) {
            return null;
        }
        TypedQuery<City> query = em().createNamedQuery("City.findByZipCode", City.class);
        query.setParameter("zipCode", zipCode);
        return query.getResultStream().findFirst().orElse(null);
    }

    public Set<City> findByCityName(String cityName) {
        if (cityName == null) {
            return Collections.emptySet();
        }
        String pattern = "%" + cityName.trim() + "%";
        TypedQuery<City> query = em().createNamedQuery("City.findByCityName", City.class);
        query.setParameter("cityName", pattern);
        return new LinkedHashSet<>(query.getResultList());
    }
}