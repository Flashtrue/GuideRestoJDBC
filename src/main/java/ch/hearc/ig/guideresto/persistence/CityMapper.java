package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.City;
import jakarta.persistence.TypedQuery;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Mapper pour les entités de type {@link City}.
 */
public class CityMapper extends AbstractMapper<City> {

    /**
     * Constructeur par défaut initialisant le mapper pour les villes.
     */
    public CityMapper() {
        super(City.class);
    }

    /**
     * Récupère toutes les villes.
     *
     * @return un ensemble de toutes les villes.
     */
    @Override
    public Set<City> findAll() {
        TypedQuery<City> query = em().createNamedQuery("City.findAll", City.class);
        return new LinkedHashSet<>(query.getResultList());
    }

    /**
     * Récupère une ville à partir de son code postal.
     *
     * @param zipCode le code postal de la ville recherchée.
     * @return la ville correspondante, ou null si aucun code postal n'est fourni.
     */
    public City findByZipCode(String zipCode) {
        if (zipCode == null) {
            return null;
        }
        TypedQuery<City> query = em().createNamedQuery("City.findByZipCode", City.class);
        query.setParameter("zipCode", zipCode);
        return query.getResultStream().findFirst().orElse(null);
    }

    /**
     * Récupère les villes correspondant à un nom donné.
     *
     * @param cityName le nom ou une partie du nom de la ville recherchée.
     * @return un ensemble des villes correspondantes, ou un ensemble vide si aucun nom n'est fourni.
     */
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