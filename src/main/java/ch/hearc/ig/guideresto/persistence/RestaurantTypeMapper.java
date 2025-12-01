package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.RestaurantType;
import jakarta.persistence.TypedQuery;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class RestaurantTypeMapper extends AbstractMapper<RestaurantType> {

    public RestaurantTypeMapper() {
        super(RestaurantType.class);
    }

    @Override
    public Set<RestaurantType> findAll() {
        TypedQuery<RestaurantType> query = em().createNamedQuery("RestaurantType.findAll", RestaurantType.class);
        return new LinkedHashSet<>(query.getResultList());
    }

    public Set<RestaurantType> findByLabel(String label) {
        if (label == null) {
            return Collections.emptySet();
        }
        String pattern = "%" + label.trim() + "%";
        TypedQuery<RestaurantType> query = em().createNamedQuery("RestaurantType.findByLabel", RestaurantType.class);
        query.setParameter("label", pattern);
        return new LinkedHashSet<>(query.getResultList());
    }
}