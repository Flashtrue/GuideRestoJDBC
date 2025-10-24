package ch.hearc.ig.guideresto.presentation;

import ch.hearc.ig.guideresto.business.*;
import ch.hearc.ig.guideresto.services.Services;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.*;

/**
 * @author cedric.baudet
 * @author alain.matile
 */
public class Application {

    private static Scanner scanner;
    private static final Logger logger = LogManager.getLogger(Application.class);
    private static Services services;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        services = new Services();

        System.out.println("Bienvenue dans GuideResto ! Que souhaitez-vous faire ?");
        int choice;
        do {
            printMainMenu();
            choice = readInt();
            proceedMainMenu(choice);
        } while (choice != 0);

        // Fermer proprement la connexion à la fin de l'application
        services.closeConnection();
    }

    /**
     * Affichage du menu principal de l'application
     */
    private static void printMainMenu() {
        System.out.println("======================================================");
        System.out.println("Que voulez-vous faire ?");
        System.out.println("1. Afficher la liste de tous les restaurants");
        System.out.println("2. Rechercher un restaurant par son nom");
        System.out.println("3. Rechercher un restaurant par ville");
        System.out.println("4. Rechercher un restaurant par son type de cuisine");
        System.out.println("5. Saisir un nouveau restaurant");
        System.out.println("0. Quitter l'application");
    }

    /**
     * On gère le choix saisi par l'utilisateur
     *
     * @param choice Un nombre entre 0 et 5.
     */
    private static void proceedMainMenu(int choice) {
        switch (choice) {
            case 1:
                showRestaurantsList();
                break;
            case 2:
                searchRestaurantByName();
                break;
            case 3:
                searchRestaurantByCity();
                break;
            case 4:
                searchRestaurantByType();
                break;
            case 5:
                addNewRestaurant();
                break;
            case 0:
                System.out.println("Au revoir !");
                break;
            default:
                System.out.println("Erreur : saisie incorrecte. Veuillez réessayer");
                break;
        }
    }

    /**
     * On affiche à l'utilisateur une liste de restaurants numérotés, et il doit en sélectionner un !
     *
     * @param restaurants Liste à afficher
     * @return L'instance du restaurant choisi par l'utilisateur
     */
    private static Restaurant pickRestaurant(Set<Restaurant> restaurants) {
        if (restaurants.isEmpty()) { // Si la liste est vide on s'arrête là
            System.out.println("Aucun restaurant n'a été trouvé !");
            return null;
        }

        String result;
        for (Restaurant currentRest : restaurants) {
            result = "";
            result = "\"" + result + currentRest.getName() + "\" - " + currentRest.getAddress().getStreet() + " - ";
            result = result + currentRest.getAddress().getCity().getZipCode() + " " + currentRest.getAddress().getCity().getCityName();
            System.out.println(result);
        }

        System.out.println("Veuillez saisir le nom exact du restaurant dont vous voulez voir le détail, ou appuyez sur Enter pour revenir en arrière");
        String choice = readString();

        if (choice.isEmpty()) {
            return null;
        }

        // Rechercher le restaurant par son nom
        for (Restaurant restaurant : restaurants) {
            if (restaurant.getName().equalsIgnoreCase(choice)) {
                return restaurant;
            }
        }

        System.out.println("Restaurant non trouvé !");
        return null;
    }

    /**
     * Affiche la liste de tous les restaurants, sans filtre
     */
    private static void showRestaurantsList() {
        System.out.println("Liste des restaurants : ");

        Set<Restaurant> restaurants = services.getRestaurantService().getAllRestaurants();
        Restaurant restaurant = pickRestaurant(restaurants);

        if (restaurant != null) {
            showRestaurant(restaurant);
        }
    }

    /**
     * Affiche une liste de restaurants dont le nom contient une chaîne de caractères saisie par l'utilisateur
     */
    private static void searchRestaurantByName() {
        System.out.println("Veuillez entrer une partie du nom recherché : ");
        String research = readString();

        Set<Restaurant> filteredList = services.getRestaurantService().findByName(research);
        Restaurant restaurant = pickRestaurant(filteredList);

        if (restaurant != null) {
            showRestaurant(restaurant);
        }
    }

    /**
     * Affiche une liste de restaurants dont le nom de la ville contient une chaîne de caractères saisie par l'utilisateur
     */
    private static void searchRestaurantByCity() {
        System.out.println("Veuillez entrer une partie du nom de la ville désirée : ");
        String research = readString();

        Set<Restaurant> filteredList = services.getRestaurantService().findByCity(research);
        Restaurant restaurant = pickRestaurant(filteredList);

        if (restaurant != null) {
            showRestaurant(restaurant);
        }
    }

    /**
     * L'utilisateur choisit une ville parmi celles présentes dans le système.
     *
     * @param cities La liste des villes à présenter à l'utilisateur
     * @return La ville sélectionnée, ou null si aucune ville n'a été choisie.
     */
    private static City pickCity(Set<City> cities) {
        System.out.println("Voici la liste des villes possibles, veuillez entrer le NPA de la ville désirée : ");

        for (City currentCity : cities) {
            System.out.println(currentCity.getZipCode() + " " + currentCity.getCityName());
        }
        System.out.println("Entrez \"NEW\" pour créer une nouvelle ville");
        String choice = readString();

        if (choice.equals("NEW")) {
            City city = new City();
            System.out.println("Veuillez entrer le NPA de la nouvelle ville : ");
            city.setZipCode(readString());
            System.out.println("Veuillez entrer le nom de la nouvelle ville : ");
            city.setCityName(readString());
            return services.getCityService().create(city);
        }

        return services.getCityService().findByZipCode(choice);
    }

    /**
     * L'utilisateur choisit un type de restaurant parmis ceux présents dans le système.
     *
     * @param types La liste des types de restaurant à présnter à l'utilisateur
     * @return Le type sélectionné, ou null si aucun type n'a été choisi.
     */
    private static RestaurantType pickRestaurantType(Set<RestaurantType> types) {
        System.out.println("Voici la liste des types possibles, veuillez entrer le libellé exact du type désiré : ");
        for (RestaurantType currentType : types) {
            System.out.println("\"" + currentType.getLabel() + "\" : " + currentType.getDescription());
        }
        String choice = readString();

        return services.getRestaurantTypeService().findByLabel(choice);
    }

    /**
     * L'utilisateur commence par sélectionner un type de restaurant, puis sélectionne un des restaurants proposés s'il y en a.
     * Si l'utilisateur sélectionne un restaurant, ce dernier lui sera affiché.
     */
    private static void searchRestaurantByType() {
        RestaurantType chosenType = pickRestaurantType(services.getRestaurantTypeService().getAll());

        if (chosenType == null) {
            System.out.println("Type de restaurant non trouvé.");
            return;
        }

        Set<Restaurant> filteredList = services.getRestaurantService().findByType(chosenType);
        Restaurant restaurant = pickRestaurant(filteredList);

        if (restaurant != null) {
            showRestaurant(restaurant);
        }
    }

    /**
     * Le programme demande les informations nécessaires à l'utilisateur puis crée un nouveau restaurant dans le système.
     */
    private static void addNewRestaurant() {
        System.out.println("Vous allez ajouter un nouveau restaurant !");
        System.out.println("Quel est son nom ?");
        String name = readString();
        System.out.println("Veuillez entrer une courte description : ");
        String description = readString();
        System.out.println("Veuillez entrer l'adresse de son site internet : ");
        String website = readString();
        System.out.println("Rue : ");
        String street = readString();

        City city = null;
        do { // La sélection d'une ville est obligatoire
            city = pickCity(services.getCityService().getAll());
        } while (city == null);

        RestaurantType restaurantType = null;
        do { // La sélection d'un type est obligatoire
            restaurantType = pickRestaurantType(services.getRestaurantTypeService().getAll());
        } while (restaurantType == null);

        // Création du restaurant
        Localisation address = new Localisation(street, city);
        Restaurant restaurant = new Restaurant(null, name, description, website, street, city, restaurantType);
        restaurant.setAddress(address);

        restaurant = services.getRestaurantService().create(restaurant);

        if (restaurant != null) {
            System.out.println("Restaurant créé avec succès !");
            showRestaurant(restaurant);
        } else {
            System.out.println("Erreur lors de la création du restaurant !");
        }
    }

    /**
     * Affiche toutes les informations du restaurant passé en paramètre, puis affiche le menu des actions disponibles sur ledit restaurant
     *
     * @param restaurant Le restaurant à afficher
     */
    private static void showRestaurant(Restaurant restaurant) {
        System.out.println("Affichage d'un restaurant : ");
        StringBuilder sb = new StringBuilder();
        sb.append(restaurant.getName()).append("\n");
        sb.append(restaurant.getDescription()).append("\n");
        sb.append(restaurant.getType().getLabel()).append("\n");
        sb.append(restaurant.getWebsite()).append("\n");
        sb.append(restaurant.getAddress().getStreet()).append(", ");
        sb.append(restaurant.getAddress().getCity().getZipCode()).append(" ").append(restaurant.getAddress().getCity().getCityName()).append("\n");
        sb.append("Nombre de likes : ").append(services.getBasicEvaluationService().countLikes(restaurant, true)).append("\n");
        sb.append("Nombre de dislikes : ").append(services.getBasicEvaluationService().countLikes(restaurant, false)).append("\n");
        sb.append("\nEvaluations reçues : ").append("\n");

        // Récupérer les évaluations complètes du restaurant
        Set<CompleteEvaluation> evaluations = services.getCompleteEvaluationService().getCompleteEvaluationsWithGrades(restaurant);

        for (CompleteEvaluation evaluation : evaluations) {
            sb.append("Evaluation de : ").append(evaluation.getUsername()).append("\n");
            sb.append("Commentaire : ").append(evaluation.getComment()).append("\n");
            for (Grade grade : evaluation.getGrades()) {
                sb.append(grade.getCriteria().getName()).append(" : ").append(grade.getGrade()).append("/5").append("\n");
            }
            sb.append("\n");
        }

        System.out.println(sb);

        int choice;
        do { // Tant que l'utilisateur n'entre pas 0 ou 6, on lui propose à nouveau les actions
            showRestaurantMenu();
            choice = readInt();
            proceedRestaurantMenu(choice, restaurant);
        } while (choice != 0 && choice != 6); // 6 car le restaurant est alors supprimé...
    }

    /**
     * Affiche dans la console un ensemble d'actions réalisables sur le restaurant actuellement sélectionné !
     */
    private static void showRestaurantMenu() {
        System.out.println("======================================================");
        System.out.println("Que souhaitez-vous faire ?");
        System.out.println("1. J'aime ce restaurant !");
        System.out.println("2. Je n'aime pas ce restaurant !");
        System.out.println("3. Faire une évaluation complète de ce restaurant !");
        System.out.println("4. Editer ce restaurant");
        System.out.println("5. Editer l'adresse du restaurant");
        System.out.println("6. Supprimer ce restaurant");
        System.out.println("0. Revenir au menu principal");
    }

    /**
     * Traite le choix saisi par l'utilisateur
     *
     * @param choice     Un numéro d'action, entre 0 et 6. Si le numéro ne se trouve pas dans cette plage, l'application ne fait rien et va réafficher le menu complet.
     * @param restaurant L'instance du restaurant sur lequel l'action doit être réalisée
     */
    private static void proceedRestaurantMenu(int choice, Restaurant restaurant) {
        switch (choice) {
            case 1:
                addBasicEvaluation(restaurant, true);
                break;
            case 2:
                addBasicEvaluation(restaurant, false);
                break;
            case 3:
                evaluateRestaurant(restaurant);
                break;
            case 4:
                editRestaurant(restaurant);
                break;
            case 5:
                editRestaurantAddress(restaurant);
                break;
            case 6:
                deleteRestaurant(restaurant);
                break;
            case 0:
                break;
            default:
                break;
        }
    }

    /**
     * Ajoute au restaurant passé en paramètre un like ou un dislike, en fonction du second paramètre.
     *
     * @param restaurant Le restaurant qui est évalué
     * @param like       Est-ce un like ou un dislike ?
     */
    private static void addBasicEvaluation(Restaurant restaurant, Boolean like) {
        String ipAddress;
        try {
            ipAddress = Inet4Address.getLocalHost().toString(); // Permet de retrouver l'adresse IP locale de l'utilisateur.
        } catch (UnknownHostException ex) {
            logger.error("Error - Couldn't retreive host IP address");
            ipAddress = "Indisponible";
        }

        BasicEvaluation eval = services.getBasicEvaluationService().create(restaurant, like, ipAddress);

        if (eval != null) {
            System.out.println("Votre vote a été pris en compte !");
        } else {
            System.out.println("Une erreur est survenue lors de l'enregistrement de votre vote !");
        }
    }

    /**
     * Crée une évaluation complète pour le restaurant. L'utilisateur doit saisir toutes les informations (dont un commentaire et quelques notes)
     *
     * @param restaurant Le restaurant à évaluer
     */
    private static void evaluateRestaurant(Restaurant restaurant) {
        System.out.println("Merci d'évaluer ce restaurant !");
        System.out.println("Quel est votre nom d'utilisateur ? ");
        String username = readString();
        System.out.println("Quel commentaire aimeriez-vous publier ?");
        String comment = readString();

        Set<Grade> grades = new HashSet<>();
        Grade grade;

        System.out.println("Veuillez svp donner une note entre 1 et 5 pour chacun de ces critères : ");
        for (EvaluationCriteria currentCriteria : services.getEvaluationCriteriaService().getAll()) {
            System.out.println(currentCriteria.getName() + " : " + currentCriteria.getDescription());
            Integer note = readInt();
            grade = new Grade(null, note, null, currentCriteria); // L'évaluation sera définie lors de la création
            grades.add(grade);
        }

        CompleteEvaluation eval = services.getCompleteEvaluationService().create(restaurant, username, comment, grades);

        if (eval != null) {
            System.out.println("Votre évaluation a bien été enregistrée, merci !");
        } else {
            System.out.println("Une erreur est survenue lors de l'enregistrement de votre évaluation !");
        }
    }

    /**
     * Force l'utilisateur à saisir à nouveau toutes les informations du restaurant (sauf la clé primaire) pour le mettre à jour.
     * Par soucis de simplicité, l'utilisateur doit tout resaisir.
     *
     * @param restaurant Le restaurant à modifier
     */
    private static void editRestaurant(Restaurant restaurant) {
        System.out.println("Edition d'un restaurant !");

        System.out.println("Nouveau nom : ");
        restaurant.setName(readString());
        System.out.println("Nouvelle description : ");
        restaurant.setDescription(readString());
        System.out.println("Nouveau site web : ");
        restaurant.setWebsite(readString());
        System.out.println("Nouveau type de restaurant : ");

        RestaurantType newType = pickRestaurantType(services.getRestaurantTypeService().getAll());
        if (newType != null && !newType.equals(restaurant.getType())) {
            restaurant.setType(newType);
        }

        boolean success = services.getRestaurantService().update(restaurant);

        if (success) {
            System.out.println("Merci, le restaurant a bien été modifié !");
        } else {
            System.out.println("Une erreur est survenue lors de la modification du restaurant !");
        }
    }

    /**
     * Permet à l'utilisateur de mettre à jour l'adresse du restaurant.
     * Par soucis de simplicité, l'utilisateur doit tout resaisir.
     *
     * @param restaurant Le restaurant dont l'adresse doit être mise à jour.
     */
    private static void editRestaurantAddress(Restaurant restaurant) {
        System.out.println("Edition de l'adresse d'un restaurant !");

        System.out.println("Nouvelle rue : ");
        restaurant.getAddress().setStreet(readString());

        City newCity = pickCity(services.getCityService().getAll());
        if (newCity != null && !newCity.equals(restaurant.getAddress().getCity())) {
            restaurant.getAddress().setCity(newCity);
        }

        boolean success = services.getRestaurantService().update(restaurant);

        if (success) {
            System.out.println("L'adresse a bien été modifiée ! Merci !");
        } else {
            System.out.println("Une erreur est survenue lors de la modification de l'adresse !");
        }
    }

    /**
     * Après confirmation par l'utilisateur, supprime complètement le restaurant et toutes ses évaluations du référentiel.
     *
     * @param restaurant Le restaurant à supprimer.
     */
    private static void deleteRestaurant(Restaurant restaurant) {
        System.out.println("Etes-vous sûr de vouloir supprimer ce restaurant ? (O/n)");
        String choice = readString();
        if (choice.equals("o") || choice.equals("O")) {
            boolean success = services.getRestaurantService().delete(restaurant);

            if (success) {
                System.out.println("Le restaurant a bien été supprimé !");
            } else {
                System.out.println("Une erreur est survenue lors de la suppression du restaurant !");
            }
        }
    }

    /**
     * readInt ne repositionne pas le scanner au début d'une ligne donc il faut le faire manuellement sinon
     * des problèmes apparaissent quand on demande à l'utilisateur de saisir une chaîne de caractères.
     *
     * @return Un nombre entier saisi par l'utilisateur au clavier
     */
    private static int readInt() {
        int i = 0;
        boolean success = false;
        do { // Tant que l'utilisateur n'aura pas saisi un nombre entier, on va lui demander une nouvelle saisie
            try {
                i = scanner.nextInt();
                success = true;
            } catch (InputMismatchException e) {
                System.out.println("Erreur ! Veuillez entrer un nombre entier s'il vous plaît !");
            } finally {
                scanner.nextLine();
            }

        } while (!success);

        return i;
    }

    /**
     * Méthode readString pour rester consistant avec readInt !
     *
     * @return Une chaîne de caractères saisie par l'utilisateur au clavier
     */
    private static String readString() {
        return scanner.nextLine();
    }
}