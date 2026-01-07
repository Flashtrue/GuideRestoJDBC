package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.CompleteEvaluation;
import ch.hearc.ig.guideresto.business.Restaurant;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Mapper pour les évaluations complètes, permettant des opérations de récupération sur la base de données.
 */
public class CompleteEvaluationMapper extends AbstractMapper<CompleteEvaluation> {

    /**
     * Constructeur par défaut initialisant le mapper pour les évaluations complètes.
     */
    public CompleteEvaluationMapper() {
        super(CompleteEvaluation.class);
    }

    /**
     * Récupère toutes les évaluations complètes.
     *
     * @return un ensemble de toutes les évaluations complètes.
     */
    @Override
    public Set<CompleteEvaluation> findAll() {
        return new LinkedHashSet<>(em()
                .createNamedQuery("CompleteEvaluation.findAll", CompleteEvaluation.class)
                .getResultList());
    }

    /**
     * Récupère les évaluations complètes associées à un restaurant donné.
     *
     * @param restaurant le restaurant pour lequel récupérer les évaluations.
     * @return un ensemble des évaluations complètes du restaurant, ou un ensemble vide si aucun restaurant n'est fourni.
     */
    public Set<CompleteEvaluation> findByRestaurant(Restaurant restaurant) {
        if (restaurant == null) {
            return Collections.emptySet();
        }
        return new LinkedHashSet<>(em()
                .createNamedQuery("CompleteEvaluation.findByRestaurant", CompleteEvaluation.class)
                .setParameter("restaurant", restaurant)
                .getResultList());
    }

    /**
     * Récupère les évaluations complètes associées à un nom d'utilisateur donné.
     *
     * @param username le nom d'utilisateur pour lequel récupérer les évaluations.
     * @return un ensemble des évaluations complètes correspondant, ou un ensemble vide si aucun nom n'est fourni.
     */
    public Set<CompleteEvaluation> findByUsername(String username) {
        if (username == null) {
            return Collections.emptySet();
        }
        String pattern = "%" + username.trim() + "%";
        return new LinkedHashSet<>(em()
                .createNamedQuery("CompleteEvaluation.findByUsername", CompleteEvaluation.class)
                .setParameter("username", pattern)
                .getResultList());
    }

    /**
     * Récupère les évaluations complètes associées à l'identifiant d'un restaurant donné.
     *
     * @param restaurantId l'identifiant du restaurant pour lequel récupérer les évaluations.
     * @return un ensemble des évaluations complètes correspondant, ou un ensemble vide si l'identifiant est invalide.
     */
    public Set<CompleteEvaluation> findByRestaurantId(int restaurantId) {
        if (restaurantId <= 0) {
            return Collections.emptySet();
        }
        return new LinkedHashSet<>(em()
                .createQuery("SELECT c FROM CompleteEvaluation c WHERE c.restaurant.id = :restaurantId", CompleteEvaluation.class)
                .setParameter("restaurantId", restaurantId)
                .getResultList());
    }
}