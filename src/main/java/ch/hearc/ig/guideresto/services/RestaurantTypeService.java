package ch.hearc.ig.guideresto.services;

import ch.hearc.ig.guideresto.business.RestaurantType;
import ch.hearc.ig.guideresto.persistence.RestaurantTypeMapper;

import java.util.Set;

/**
 * Service gérant les opérations sur les types de restaurants.
 */
public class RestaurantTypeService extends AbstractService {

    private final RestaurantTypeMapper restaurantTypeMapper = new RestaurantTypeMapper();

    /**
     * Récupère tous les types de restaurants.
     * 
     * @return l'ensemble des types de restaurants
     */
    public Set<RestaurantType> getAll() {
        return restaurantTypeMapper.findAll();
    }

    /**
     * Recherche un type de restaurant par son identifiant.
     * 
     * @param id l'identifiant du type
     * @return le type trouvé ou null si non trouvé
     */
    public RestaurantType findById(int id) {
        return restaurantTypeMapper.findById(id);
    }

    /**
     * Recherche un type de restaurant par son libellé.
     * 
     * @param label le libellé du type (recherche partielle insensible à la casse)
     * @return le premier type correspondant ou null si aucun trouvé
     */
    public RestaurantType findByLabel(String label) {
        Set<RestaurantType> types = restaurantTypeMapper.findByLabel(label);
        return types.isEmpty() ? null : types.iterator().next();
    }

    /**
     * Crée un nouveau type de restaurant.
     * 
     * @param type le type à créer
     * @return le type créé ou null en cas d'erreur
     */
    public RestaurantType create(RestaurantType type) {
        return restaurantTypeMapper.create(type);
    }

    /**
     * Met à jour un type de restaurant existant.
     * 
     * @param type le type à mettre à jour
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean update(RestaurantType type) {
        return restaurantTypeMapper.update(type);
    }

    /**
     * Supprime un type de restaurant.
     * 
     * @param type le type à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean delete(RestaurantType type) {
        return restaurantTypeMapper.delete(type);
    }
}