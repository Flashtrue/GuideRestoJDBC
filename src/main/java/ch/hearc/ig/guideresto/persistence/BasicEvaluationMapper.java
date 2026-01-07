package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.BasicEvaluation;
import ch.hearc.ig.guideresto.business.Restaurant;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class BasicEvaluationMapper extends AbstractMapper<BasicEvaluation> {
    /**
     * Constructeur par défaut initialisant le mapper pour les évaluations basiques.
     */
    public BasicEvaluationMapper() {
        super(BasicEvaluation.class);
    }

    /**
     * Récupère toutes les évaluations basiques.
     *
     * @return un ensemble de toutes les évaluations basiques.
     */
    @Override
    public Set<BasicEvaluation> findAll() {
        return new LinkedHashSet<>(em()
                .createNamedQuery("BasicEvaluation.findAll", BasicEvaluation.class)
                .getResultList());
    }

    /**
     * Récupère les évaluations basiques associées à un restaurant donné.
     *
     * @param restaurant le restaurant pour lequel récupérer les évaluations.
     * @return un ensemble des évaluations basiques du restaurant, ou un ensemble vide si aucun restaurant n'est fourni.
     */
    public Set<BasicEvaluation> findByRestaurant(Restaurant restaurant) {
        if (restaurant == null) {
            return Collections.emptySet();
        }
        return new LinkedHashSet<>(em()
                .createNamedQuery("BasicEvaluation.findByRestaurant", BasicEvaluation.class)
                .setParameter("restaurant", restaurant)
                .getResultList());
    }

    /**
     * Récupère les évaluations basiques selon l'appréciation du restaurant.
     *
     * @param likeRestaurant indique si le restaurant est apprécié ou non.
     * @return un ensemble des évaluations basiques correspondant, ou un ensemble vide si aucun critère n'est fourni.
     */
    public Set<BasicEvaluation> findByLikeRestaurant(Boolean likeRestaurant) {
        if (likeRestaurant == null) {
            return Collections.emptySet();
        }
        return new LinkedHashSet<>(em()
                .createNamedQuery("BasicEvaluation.findByLikeRestaurant", BasicEvaluation.class)
                .setParameter("likeRestaurant", likeRestaurant)
                .getResultList());
    }

    /**
     * Récupère les évaluations basiques associées à une adresse IP donnée.
     *
     * @param ipAddress l'adresse IP pour laquelle récupérer les évaluations.
     * @return un ensemble des évaluations basiques correspondant, ou un ensemble vide si aucune adresse IP n'est fournie.
     */
    public Set<BasicEvaluation> findByIpAddress(String ipAddress) {
        if (ipAddress == null) {
            return Collections.emptySet();
        }
        return new LinkedHashSet<>(em()
                .createNamedQuery("BasicEvaluation.findByIpAddress", BasicEvaluation.class)
                .setParameter("ipAddress", ipAddress)
                .getResultList());
    }
}