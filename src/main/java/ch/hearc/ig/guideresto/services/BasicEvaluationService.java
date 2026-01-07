package ch.hearc.ig.guideresto.services;

import ch.hearc.ig.guideresto.business.*;
import ch.hearc.ig.guideresto.persistence.BasicEvaluationMapper;

import java.util.Date;
import java.util.Set;

/**
 * Service gérant les opérations sur les évaluations basiques (like/dislike).
 */
public class BasicEvaluationService extends AbstractService {

    private final BasicEvaluationMapper basicEvaluationMapper = new BasicEvaluationMapper();

    /**
     * Récupère toutes les évaluations basiques.
     * 
     * @return l'ensemble des évaluations basiques
     */
    public Set<BasicEvaluation> getAll() {
        return basicEvaluationMapper.findAll();
    }

    /**
     * Recherche une évaluation basique par son identifiant.
     * 
     * @param id l'identifiant de l'évaluation
     * @return l'évaluation trouvée ou null si non trouvée
     */
    public BasicEvaluation findById(int id) {
        return basicEvaluationMapper.findById(id);
    }

    /**
     * Récupère toutes les évaluations basiques pour un restaurant donné.
     * 
     * @param restaurant le restaurant concerné
     * @return l'ensemble des évaluations du restaurant
     */
    public Set<BasicEvaluation> findByRestaurant(Restaurant restaurant) {
        return basicEvaluationMapper.findByRestaurant(restaurant);
    }

    /**
     * Crée une nouvelle évaluation basique pour un restaurant.
     * 
     * @param restaurant le restaurant évalué
     * @param like true si l'utilisateur aime le restaurant, false sinon
     * @param ipAddress l'adresse IP de l'utilisateur
     * @return l'évaluation créée ou null en cas d'erreur
     */
    public BasicEvaluation create(Restaurant restaurant, boolean like, String ipAddress) {
        try {
            BasicEvaluation evaluation = new BasicEvaluation(null, new Date(), restaurant, like, ipAddress);
            BasicEvaluation created = basicEvaluationMapper.create(evaluation);
            if (created != null) {
                restaurant.getEvaluations().add(created);
            }
            return created;
        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'évaluation basique", e);
            return null;
        }
    }

    /**
     * Met à jour une évaluation basique existante.
     * 
     * @param evaluation l'évaluation à mettre à jour
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean update(BasicEvaluation evaluation) {
        return basicEvaluationMapper.update(evaluation);
    }

    /**
     * Supprime une évaluation basique.
     * 
     * @param evaluation l'évaluation à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean delete(BasicEvaluation evaluation) {
        return basicEvaluationMapper.delete(evaluation);
    }

    /**
     * Compte le nombre de likes ou dislikes pour un restaurant.
     * 
     * @param restaurant le restaurant concerné
     * @param likeRestaurant true pour compter les likes, false pour les dislikes
     * @return le nombre d'évaluations correspondantes
     */
    public int countLikes(Restaurant restaurant, boolean likeRestaurant) {
        return (int) findByRestaurant(restaurant).stream()
                .filter(eval -> eval.getLikeRestaurant() == likeRestaurant)
                .count();
    }
}