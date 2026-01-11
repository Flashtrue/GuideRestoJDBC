package ch.hearc.ig.guideresto.services;

import ch.hearc.ig.guideresto.business.City;
import ch.hearc.ig.guideresto.persistence.CityMapper;

import java.util.Set;

/**
 * Service gérant les opérations sur les villes.
 */
public class CityService extends AbstractService {

    private final CityMapper cityMapper = new CityMapper();

    /**
     * Récupère toutes les villes.
     *
     * @return l'ensemble des villes
     */
    public Set<City> getAll() {
        return cityMapper.findAll();
    }

    /**
     * Recherche une ville par son identifiant.
     *
     * @param id l'identifiant de la ville
     * @return la ville trouvée ou null si non trouvée
     */
    public City findById(int id) {
        return cityMapper.findById(id);
    }

    /**
     * Recherche une ville par son code postal.
     *
     * @param zipCode le code postal
     * @return la ville trouvée ou null si non trouvée
     */
    public City findByZipCode(String zipCode) {
        return cityMapper.findByZipCode(zipCode);
    }

    /**
     * Recherche des villes par leur nom.
     *
     * @param cityName le nom de la ville (recherche partielle insensible à la casse)
     * @return l'ensemble des villes correspondantes
     */
    public Set<City> findByCityName(String cityName) {
        return cityMapper.findByCityName(cityName);
    }

    /**
     * Crée une nouvelle ville.
     *
     * @param city la ville à créer
     * @return la ville créée ou null en cas d'erreur
     */
    public City create(City city) {
        return executeInTransactionWithResult(em -> cityMapper.create(city));
    }

    /**
     * Met à jour une ville existante.
     *
     * @param city la ville à mettre à jour
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean update(City city) {
        return executeInTransactionWithResult(em -> cityMapper.update(city));
    }

    /**
     * Supprime une ville.
     *
     * @param city la ville à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean delete(City city) {
        return executeInTransactionWithResult(em -> cityMapper.delete(city));
    }
}