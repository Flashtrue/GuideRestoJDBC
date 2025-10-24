package ch.hearc.ig.guideresto.services;

import ch.hearc.ig.guideresto.persistence.ConnectionUtils;

public class Services {

    private final RestaurantService restaurantService;
    private final CityService cityService;
    private final RestaurantTypeService restaurantTypeService;
    private final BasicEvaluationService basicEvaluationService;
    private final CompleteEvaluationService completeEvaluationService;
    private final EvaluationCriteriaService evaluationCriteriaService;
    private final GradeService gradeService;

    public Services() {
        this.restaurantService = new RestaurantService();
        this.cityService = new CityService();
        this.restaurantTypeService = new RestaurantTypeService();
        this.basicEvaluationService = new BasicEvaluationService();
        this.completeEvaluationService = new CompleteEvaluationService();
        this.evaluationCriteriaService = new EvaluationCriteriaService();
        this.gradeService = new GradeService();
    }

    // Getters pour accéder aux services spécialisés
    public RestaurantService getRestaurantService() {
        return restaurantService;
    }

    public CityService getCityService() {
        return cityService;
    }

    public RestaurantTypeService getRestaurantTypeService() {
        return restaurantTypeService;
    }

    public BasicEvaluationService getBasicEvaluationService() {
        return basicEvaluationService;
    }

    public CompleteEvaluationService getCompleteEvaluationService() {
        return completeEvaluationService;
    }

    public EvaluationCriteriaService getEvaluationCriteriaService() {
        return evaluationCriteriaService;
    }

    public GradeService getGradeService() {
        return gradeService;
    }

    public void closeConnection() {
        ConnectionUtils.closeConnection();
    }
}