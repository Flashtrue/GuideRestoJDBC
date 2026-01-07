package ch.hearc.ig.guideresto.services;

import ch.hearc.ig.guideresto.persistence.jpa.JpaUtils;

/**
 * Point d'accès centralisé à tous les services de l'application.
 */
public class Services {

    private final CityService cityService;
    private final RestaurantService restaurantService;
    private final RestaurantTypeService restaurantTypeService;
    private final BasicEvaluationService basicEvaluationService;
    private final CompleteEvaluationService completeEvaluationService;
    private final EvaluationCriteriaService evaluationCriteriaService;
    private final GradeService gradeService;

    /**
     * Initialise tous les services de l'application.
     */
    public Services() {
        this.cityService = new CityService();
        this.restaurantService = new RestaurantService();
        this.restaurantTypeService = new RestaurantTypeService();
        this.basicEvaluationService = new BasicEvaluationService();
        this.completeEvaluationService = new CompleteEvaluationService();
        this.evaluationCriteriaService = new EvaluationCriteriaService();
        this.gradeService = new GradeService();
    }

    /**
     * Fournit le service de gestion des villes.
     * 
     * @return le service des villes
     */
    public CityService getCityService() {
        return cityService;
    }

    /**
     * Fournit le service de gestion des restaurants.
     * 
     * @return le service des restaurants
     */
    public RestaurantService getRestaurantService() {
        return restaurantService;
    }

    /**
     * Fournit le service de gestion des types de restaurants.
     * 
     * @return le service des types de restaurants
     */
    public RestaurantTypeService getRestaurantTypeService() {
        return restaurantTypeService;
    }

    /**
     * Fournit le service de gestion des évaluations basiques.
     * 
     * @return le service des évaluations basiques
     */
    public BasicEvaluationService getBasicEvaluationService() {
        return basicEvaluationService;
    }

    /**
     * Fournit le service de gestion des évaluations complètes.
     * 
     * @return le service des évaluations complètes
     */
    public CompleteEvaluationService getCompleteEvaluationService() {
        return completeEvaluationService;
    }

    /**
     * Fournit le service de gestion des critères d'évaluation.
     * 
     * @return le service des critères d'évaluation
     */
    public EvaluationCriteriaService getEvaluationCriteriaService() {
        return evaluationCriteriaService;
    }

    /**
     * Fournit le service de gestion des notes.
     * 
     * @return le service des notes
     */
    public GradeService getGradeService() {
        return gradeService;
    }

    /**
     * Ferme la connexion à la base de données et libère les ressources JPA.
     */
    public void closeConnection() {
        JpaUtils.closeEntityManagerFactory();
    }
}