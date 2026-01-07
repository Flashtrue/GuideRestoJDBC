package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.City;
import ch.hearc.ig.guideresto.business.Restaurant;
import ch.hearc.ig.guideresto.business.RestaurantType;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Mapper pour la gestion des restaurants dans la base de données.
 */
public class RestaurantMapper extends AbstractMapper<Restaurant> {

    /**
     * Constructeur par défaut initialisant le mapper pour les restaurants.
     */
    public RestaurantMapper() {
        super(Restaurant.class);
    }

    /**
     * Récupère tous les restaurants.
     *
     * @return un ensemble de tous les restaurants.
     */
    @Override
    public Set<Restaurant> findAll() {
        return new LinkedHashSet<>(em().createNamedQuery("Restaurant.findAll", Restaurant.class).getResultList());
    }

    /**
     * Récupère les restaurants correspondant à un nom donné.
     *
     * @param name le nom ou une partie du nom du restaurant recherché.
     * @return un ensemble des restaurants correspondants, ou un ensemble vide si aucun nom n'est fourni.
     */
    public Set<Restaurant> findByName(String name) {
        if (name == null) {
            return Collections.emptySet();
        }
        return new LinkedHashSet<>(em()
                .createNamedQuery("Restaurant.findByName", Restaurant.class)
                .setParameter("name", "%" + name.trim() + "%")
                .getResultList());
    }

    /**
     * Récupère les restaurants correspondant à un type donné.
     *
     * @param type le type de restaurant recherché.
     * @return un ensemble des restaurants correspondants, ou un ensemble vide si aucun type n'est fourni.
     */
    public Set<Restaurant> findByType(RestaurantType type) {
        if (type == null) {
            return Collections.emptySet();
        }
        return new LinkedHashSet<>(em()
                .createNamedQuery("Restaurant.findByType", Restaurant.class)
                .setParameter("type", type)
                .getResultList());
    }

    /**
     * Récupère les restaurants situés dans une ville donnée.
     *
     * @param city la ville pour laquelle récupérer les restaurants.
     * @return un ensemble des restaurants correspondants, ou un ensemble vide si aucune ville n'est fournie.
     */
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