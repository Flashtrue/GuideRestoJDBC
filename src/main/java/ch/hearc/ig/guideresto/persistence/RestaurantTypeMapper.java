package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.RestaurantType;
import jakarta.persistence.TypedQuery;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Mapper pour les entités de type {@link RestaurantType}.
 * Permet de faire le lien entre la base de données et l'application pour les types de restaurants.
 */
public class RestaurantTypeMapper extends AbstractMapper<RestaurantType> {

    /**
     * Constructeur par défaut initialisant le mapper pour les types de restaurants.
     */
    public RestaurantTypeMapper() {
        super(RestaurantType.class);
    }

    /**
     * Récupère tous les types de restaurants.
     *
     * @return un ensemble de tous les types de restaurants.
     */
    @Override
    public Set<RestaurantType> findAll() {
        TypedQuery<RestaurantType> query = em().createNamedQuery("RestaurantType.findAll", RestaurantType.class);
        return new LinkedHashSet<>(query.getResultList());
    }

    /**
     * Récupère les types de restaurants correspondant à un libellé donné.
     *
     * @param label le libellé ou une partie du libellé recherché.
     * @return un ensemble des types de restaurants correspondants, ou un ensemble vide si aucun libellé n'est fourni.
     */
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