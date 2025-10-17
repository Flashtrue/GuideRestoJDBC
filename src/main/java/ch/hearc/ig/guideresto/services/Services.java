package ch.hearc.ig.guideresto.services;

import ch.hearc.ig.guideresto.business.*;
import ch.hearc.ig.guideresto.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe de services qui fait le pont entre la couche présentation et la couche persistance
 */
public class Services {

    private static final Logger logger = LogManager.getLogger(Services.class);
    
    // Mappers utilisés par les services
    private final RestaurantMapper restaurantMapper;
    private final CityMapper cityMapper;
    private final RestaurantTypeMapper restaurantTypeMapper;
    private final BasicEvaluationMapper basicEvaluationMapper;
    private final CompleteEvaluationMapper completeEvaluationMapper;
    private final EvaluationCriteriaMapper evaluationCriteriaMapper;
    private final GradeMapper gradeMapper;

    public Services() {
        // Initialisation des mappers
        this.restaurantMapper = new RestaurantMapper();
        this.cityMapper = new CityMapper();
        this.restaurantTypeMapper = new RestaurantTypeMapper();
        this.basicEvaluationMapper = new BasicEvaluationMapper();
        this.completeEvaluationMapper = new CompleteEvaluationMapper();
        this.evaluationCriteriaMapper = new EvaluationCriteriaMapper();
        this.gradeMapper = new GradeMapper();
    }

    /**
     * Récupère tous les restaurants de la base de données
     * @return Un ensemble de tous les restaurants
     */
    public Set<Restaurant> getAllRestaurants() {
        return restaurantMapper.findAll();
    }
    
    /**
     * Recherche les restaurants dont le nom contient la chaîne spécifiée
     * @param name La chaîne à rechercher dans le nom des restaurants
     * @return Un ensemble des restaurants correspondants
     */
    public Set<Restaurant> findRestaurantsByName(String name) {
        Set<Restaurant> allRestaurants = getAllRestaurants();
        Set<Restaurant> filteredRestaurants = new HashSet<>();
        
        for (Restaurant restaurant : allRestaurants) {
            if (restaurant.getName().toUpperCase().contains(name.toUpperCase())) {
                filteredRestaurants.add(restaurant);
            }
        }
        
        return filteredRestaurants;
    }
    
    /**
     * Recherche les restaurants situés dans une ville dont le nom contient la chaîne spécifiée
     * @param cityName La chaîne à rechercher dans le nom des villes
     * @return Un ensemble des restaurants correspondants
     */
    public Set<Restaurant> findRestaurantsByCity(String cityName) {
        Set<Restaurant> allRestaurants = getAllRestaurants();
        Set<Restaurant> filteredRestaurants = new HashSet<>();
        
        for (Restaurant restaurant : allRestaurants) {
            if (restaurant.getAddress().getCity().getCityName().toUpperCase().contains(cityName.toUpperCase())) {
                filteredRestaurants.add(restaurant);
            }
        }
        
        return filteredRestaurants;
    }
    
    /**
     * Recherche les restaurants d'un type spécifique
     * @param type Le type de restaurant à rechercher
     * @return Un ensemble des restaurants correspondants
     */
    public Set<Restaurant> findRestaurantsByType(RestaurantType type) {
        Set<Restaurant> allRestaurants = getAllRestaurants();
        Set<Restaurant> filteredRestaurants = new HashSet<>();
        
        for (Restaurant restaurant : allRestaurants) {
            if (restaurant.getType().getId().equals(type.getId())) {
                filteredRestaurants.add(restaurant);
            }
        }
        
        return filteredRestaurants;
    }
    
    /**
     * Récupère toutes les villes de la base de données
     * @return Un ensemble de toutes les villes
     */
    public Set<City> getAllCities() {
        return cityMapper.findAll();
    }
    
    /**
     * Récupère tous les types de restaurants de la base de données
     * @return Un ensemble de tous les types de restaurants
     */
    public Set<RestaurantType> getAllRestaurantTypes() {
        return restaurantTypeMapper.findAll();
    }
    
    /**
     * Récupère tous les critères d'évaluation de la base de données
     * @return Un ensemble de tous les critères d'évaluation
     */
    public Set<EvaluationCriteria> getAllEvaluationCriteria() {
        return evaluationCriteriaMapper.findAll();
    }
    
    /**
     * Ajoute un nouveau restaurant dans la base de données
     * @param restaurant Le restaurant à ajouter
     * @return Le restaurant ajouté avec son ID généré
     */
    public Restaurant createRestaurant(Restaurant restaurant) {
        Connection connection = ConnectionUtils.getConnection();
        
        try {
            // Désactivation de l'auto-commit pour gérer la transaction
            connection.setAutoCommit(false);
            
            // Création du restaurant
            Restaurant createdRestaurant = restaurantMapper.create(restaurant);
            
            // Validation de la transaction
            connection.commit();
            
            return createdRestaurant;
        } catch (SQLException e) {
            // En cas d'erreur, on annule la transaction
            try {
                connection.rollback();
            } catch (SQLException ex) {
                logger.error("Erreur lors du rollback", ex);
            }
            logger.error("Erreur lors de la création du restaurant", e);
            return null;
        }
    }
    
    /**
     * Met à jour un restaurant existant dans la base de données
     * @param restaurant Le restaurant à mettre à jour
     * @return true si la mise à jour a réussi, false sinon
     */
    public boolean updateRestaurant(Restaurant restaurant) {
        Connection connection = ConnectionUtils.getConnection();
        
        try {
            // Désactivation de l'auto-commit pour gérer la transaction
            connection.setAutoCommit(false);
            
            // Mise à jour du restaurant
            boolean result = restaurantMapper.update(restaurant);
            
            // Validation de la transaction
            connection.commit();
            
            return result;
        } catch (SQLException e) {
            // En cas d'erreur, on annule la transaction
            try {
                connection.rollback();
            } catch (SQLException ex) {
                logger.error("Erreur lors du rollback", ex);
            }
            logger.error("Erreur lors de la mise à jour du restaurant", e);
            return false;
        }
    }
    
    /**
     * Supprime un restaurant et toutes ses évaluations de la base de données
     * @param restaurant Le restaurant à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean deleteRestaurant(Restaurant restaurant) {
        Connection connection = ConnectionUtils.getConnection();
        
        try {
            // Désactivation de l'auto-commit pour gérer la transaction
            connection.setAutoCommit(false);
            
            // Récupération des évaluations associées
            Set<Evaluation> evaluations = new HashSet<>();
            evaluations.addAll(basicEvaluationMapper.findByRestaurant(restaurant));
            evaluations.addAll(completeEvaluationMapper.findByRestaurantId(restaurant.getId()));
            
            // Suppression de toutes les évaluations
            for (Evaluation evaluation : evaluations) {
                if (evaluation instanceof CompleteEvaluation) {
                    // Suppression des notes associées à l'évaluation complète
                    CompleteEvaluation completeEvaluation = (CompleteEvaluation) evaluation;
                    for (Grade grade : completeEvaluation.getGrades()) {
                        gradeMapper.delete(grade);
                    }
                    completeEvaluationMapper.delete(completeEvaluation);
                } else if (evaluation instanceof BasicEvaluation) {
                    basicEvaluationMapper.delete((BasicEvaluation) evaluation);
                }
            }
            
            // Suppression du restaurant
            boolean result = restaurantMapper.delete(restaurant);
            
            // Validation de la transaction
            connection.commit();
            
            return result;
        } catch (SQLException e) {
            // En cas d'erreur, on annule la transaction
            try {
                connection.rollback();
            } catch (SQLException ex) {
                logger.error("Erreur lors du rollback", ex);
            }
            logger.error("Erreur lors de la suppression du restaurant", e);
            return false;
        }
    }
    
    /**
     * Ajoute une ville à la base de données
     * @param city La ville à ajouter
     * @return La ville ajoutée avec son ID généré
     */
    public City createCity(City city) {
        Connection connection = ConnectionUtils.getConnection();
        
        try {
            connection.setAutoCommit(false);
            City createdCity = cityMapper.create(city);
            connection.commit();
            return createdCity;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                logger.error("Erreur lors du rollback", ex);
            }
            logger.error("Erreur lors de la création de la ville", e);
            return null;
        }
    }
    
    /**
     * Recherche une ville par son code postal
     * @param zipCode Le code postal à rechercher
     * @return La ville correspondante ou null si non trouvée
     */
    public City findCityByZipCode(String zipCode) {
        Set<City> cities = getAllCities();
        
        for (City city : cities) {
            if (city.getZipCode().equals(zipCode)) {
                return city;
            }
        }
        
        return null;
    }
    
    /**
     * Recherche un type de restaurant par son libellé
     * @param label Le libellé du type à rechercher
     * @return Le type de restaurant correspondant ou null si non trouvé
     */
    public RestaurantType findRestaurantTypeByLabel(String label) {
        Set<RestaurantType> types = getAllRestaurantTypes();
        
        for (RestaurantType type : types) {
            if (type.getLabel().equalsIgnoreCase(label)) {
                return type;
            }
        }
        
        return null;
    }
    
    /**
     * Ajoute une évaluation basique (like/dislike) à un restaurant
     * @param restaurant Le restaurant à évaluer
     * @param like true pour un like, false pour un dislike
     * @param ipAddress L'adresse IP de l'utilisateur
     * @return L'évaluation créée avec son ID généré
     */
    public BasicEvaluation addBasicEvaluation(Restaurant restaurant, boolean like, String ipAddress) {
        Connection connection = ConnectionUtils.getConnection();
        
        try {
            connection.setAutoCommit(false);
            
            BasicEvaluation evaluation = new BasicEvaluation(null, new Date(), restaurant, like, ipAddress);
            BasicEvaluation createdEvaluation = basicEvaluationMapper.create(evaluation);
            
            connection.commit();
            
            // Mettre à jour la collection d'évaluations du restaurant en mémoire
            restaurant.getEvaluations().add(createdEvaluation);
            
            return createdEvaluation;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                logger.error("Erreur lors du rollback", ex);
            }
            logger.error("Erreur lors de l'ajout d'une évaluation basique", e);
            return null;
        }
    }
    
    /**
     * Ajoute une évaluation complète avec des notes à un restaurant
     * @param restaurant Le restaurant à évaluer
     * @param username Le nom d'utilisateur
     * @param comment Le commentaire
     * @param grades Les notes pour chaque critère
     * @return L'évaluation complète créée avec son ID généré
     */
    public CompleteEvaluation addCompleteEvaluation(Restaurant restaurant, String username, String comment, Set<Grade> grades) {
        Connection connection = ConnectionUtils.getConnection();
        
        try {
            connection.setAutoCommit(false);
            
            // Création de l'évaluation
            CompleteEvaluation evaluation = new CompleteEvaluation(null, new Date(), restaurant, comment, username);
            CompleteEvaluation createdEvaluation = completeEvaluationMapper.create(evaluation);
            
            if (createdEvaluation == null) {
                logger.error("Échec de la création de l'évaluation");
                connection.rollback();
                return null;
            }
            
            // Ajout des notes
            Set<Grade> createdGrades = new HashSet<>();
            for (Grade grade : grades) {
                // Important: s'assurer que l'évaluation est bien liée à la note
                grade.setEvaluation(createdEvaluation);
                Grade createdGrade = gradeMapper.create(grade);
                if (createdGrade != null) {
                    createdGrades.add(createdGrade);
                } else {
                    logger.error("Échec de la création d'une note");
                    connection.rollback();
                    return null;
                }
            }
            
            createdEvaluation.setGrades(createdGrades);
            
            connection.commit();
            
            // Mettre à jour la collection d'évaluations du restaurant en mémoire
            restaurant.getEvaluations().add(createdEvaluation);
            
            return createdEvaluation;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                logger.error("Erreur lors du rollback", ex);
            }
            logger.error("Erreur lors de l'ajout d'une évaluation complète", e);
            return null;
        }
    }
    
    /**
     * Compte le nombre de likes ou dislikes pour un restaurant
     * @param restaurant Le restaurant concerné
     * @param likeRestaurant true pour compter les likes, false pour les dislikes
     * @return Le nombre de likes ou dislikes
     */
    public int countLikes(Restaurant restaurant, boolean likeRestaurant) {
        int count = 0;
        
        // Charger les évaluations du restaurant si elles ne sont pas déjà chargées
        Set<BasicEvaluation> evaluations = basicEvaluationMapper.findByRestaurant(restaurant);
        
        for (BasicEvaluation evaluation : evaluations) {
            if (evaluation.getLikeRestaurant() == likeRestaurant) {
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * Charge les évaluations complètes d'un restaurant avec leurs notes
     * @param restaurant Le restaurant dont on veut les évaluations
     * @return Un ensemble d'évaluations complètes
     */
    public Set<CompleteEvaluation> getCompleteEvaluations(Restaurant restaurant) {
        Set<CompleteEvaluation> evaluations = completeEvaluationMapper.findByRestaurantId(restaurant.getId());
        
        // Charger les notes pour chaque évaluation
        for (CompleteEvaluation evaluation : evaluations) {
            Set<Grade> grades = gradeMapper.findByEvaluationId(evaluation.getId());
            evaluation.setGrades(grades);
        }
        
        return evaluations;
    }
    
    /**
     * Ferme la connexion à la base de données
     * Cette méthode doit être appelée à la fermeture de l'application
     */
    public void closeConnection() {
            ConnectionUtils.closeConnection();
    }
}