package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.City;
import ch.hearc.ig.guideresto.business.Restaurant;
import ch.hearc.ig.guideresto.business.RestaurantType;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class RestaurantMapper extends AbstractMapper<Restaurant> {

    public RestaurantMapper() {
        super(Restaurant.class);
    }

    @Override
    public Set<Restaurant> findAll() {
        return new LinkedHashSet<>(em().createNamedQuery("Restaurant.findAll", Restaurant.class).getResultList());
    }

    public Set<Restaurant> findByName(String name) {
        if (name == null) {
            return Collections.emptySet();
        }
        return new LinkedHashSet<>(em()
                .createNamedQuery("Restaurant.findByName", Restaurant.class)
                .setParameter("name", "%" + name.trim() + "%")
                .getResultList());
    }

    public Set<Restaurant> findByType(RestaurantType type) {
        if (type == null) {
            return Collections.emptySet();
        }
        return new LinkedHashSet<>(em()
                .createNamedQuery("Restaurant.findByType", Restaurant.class)
                .setParameter("type", type)
                .getResultList());
    }

    public Set<Restaurant> findByCity(City city) {
        if (city == null) {
            return Collections.emptySet();
        }
        return new LinkedHashSet<>(em()
                .createNamedQuery("Restaurant.findByCity", Restaurant.class)
                .setParameter("city", city)
                .getResultList());
    }
}