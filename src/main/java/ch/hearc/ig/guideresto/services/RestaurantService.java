package ch.hearc.ig.guideresto.services;

import ch.hearc.ig.guideresto.business.*;
import ch.hearc.ig.guideresto.persistence.*;

import java.util.Set;

/**
 * Service gérant les opérations sur les restaurants.
 */
public class RestaurantService extends AbstractService {

    private final RestaurantMapper restaurantMapper = new RestaurantMapper();
    private final CityMapper cityMapper = new CityMapper();

    /**
     * Récupère tous les restaurants.
     *
     * @return l'ensemble des restaurants
     */
    public Set<Restaurant> getAllRestaurants() {
        return restaurantMapper.findAll();
    }

    /**
     * Recherche un restaurant par son identifiant.
     *
     * @param id l'identifiant du restaurant
     * @return le restaurant trouvé ou null si non trouvé
     */
    public Restaurant findById(int id) {
        return restaurantMapper.findById(id);
    }

    /**
     * Recherche des restaurants par leur nom.
     *
     * @param name le nom du restaurant (recherche partielle insensible à la casse)
     * @return l'ensemble des restaurants correspondants
     */
    public Set<Restaurant> findByName(String name) {
        return restaurantMapper.findByName(name);
    }

    /**
     * Recherche des restaurants par le nom de leur ville.
     *
     * @param cityName le nom de la ville
     * @return l'ensemble des restaurants de la ville ou un ensemble vide si la ville n'existe pas
     */
    public Set<Restaurant> findByCity(String cityName) {
        Set<City> cities = cityMapper.findByCityName(cityName);
        if (cities.isEmpty()) {
            return Set.of();
        }
        return restaurantMapper.findByCity(cities.iterator().next());
    }

    /**
     * Recherche des restaurants par leur type gastronomique.
     *
     * @param type le type de restaurant
     * @return l'ensemble des restaurants du type spécifié
     */
    public Set<Restaurant> findByType(RestaurantType type) {
        return restaurantMapper.findByType(type);
    }

    /**
     * Crée un nouveau restaurant avec sa ville associée de manière atomique.
     *
     * @param restaurant le restaurant à créer
     * @return le restaurant créé ou null en cas d'erreur
     */
    public Restaurant create(Restaurant restaurant) {
        return executeInTransactionWithResult(em -> {
            try {
                // 1. Gestion de la ville
                City city = restaurant.getAddress().getCity();
                if (city.getId() == null) {
                    City existingCity = cityMapper.findByZipCode(city.getZipCode());
                    if (existingCity != null) {
                        restaurant.getAddress().setCity(existingCity);
                    } else {
                        City createdCity = cityMapper.create(city);
                        restaurant.getAddress().setCity(createdCity);
                    }
                }

                // 2. Persistance du restaurant (Localisation est @Embeddable, donc pas de persist séparé)
                return restaurantMapper.create(restaurant);
            } catch (Exception e) {
                logger.error("Erreur lors de la création du restaurant", e);
                throw e; // Relancer pour rollback automatique
            }
        });
    }

    /**
     * Met à jour un restaurant existant.
     * Gère les exceptions de verrouillage optimiste.
     *
     * @param restaurant le restaurant à mettre à jour
     * @return true si la mise à jour a réussi, false sinon (notamment en cas de conflit optimiste)
     */
    public boolean update(Restaurant restaurant) {
        try {
            return executeInTransactionWithResult(em -> restaurantMapper.update(restaurant));
        } catch (jakarta.persistence.OptimisticLockException ex) {
            logger.warn("Conflit de verrouillage optimiste détecté pour le restaurant id={}", restaurant.getId(), ex);
            return false;
        } catch (Exception ex) {
            logger.error("Erreur lors de la mise à jour du restaurant id={}", restaurant.getId(), ex);
            return false;
        }
    }

    /**
     * Supprime un restaurant et toutes ses évaluations associées en cascade.
     *
     * @param restaurant le restaurant à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean delete(Restaurant restaurant) {
        try {
            return executeInTransactionWithResult(em -> restaurantMapper.delete(restaurant));
        } catch (jakarta.persistence.OptimisticLockException ex) {
            logger.warn("Conflit de verrouillage optimiste lors de la suppression du restaurant id={}", restaurant.getId());
            logger.warn("Le restaurant a été modifié ou supprimé par un autre utilisateur", ex);
            return false;
        } catch (Exception ex) {
            logger.error("Erreur lors de la suppression du restaurant id={}", restaurant.getId(), ex);
            return false;
        }
    }
}