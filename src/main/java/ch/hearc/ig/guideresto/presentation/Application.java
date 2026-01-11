package ch.hearc.ig.guideresto.presentation;

import ch.hearc.ig.guideresto.business.*;
import ch.hearc.ig.guideresto.services.Services;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Point d'entrée principal de l'application GuideResto.
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

        services.closeConnection();
    }

    /**
     * Affiche le menu principal de l'application.
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
     * Gère le choix de l'utilisateur dans le menu principal.
     *
     * @param choice un nombre entre 0 et 5 représentant l'action choisie.
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
        if (restaurants.isEmpty()) { 
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

        for (Restaurant restaurant : restaurants) {
            if (restaurant.getName().equalsIgnoreCase(choice)) {
                return restaurant;
            }
        }

        System.out.println("Restaurant non trouvé !");
        return null;
    }

    /**
     * Affiche la liste de tous les restaurants.
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
     * Recherche des restaurants par leur nom.
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
     * Recherche des restaurants par leur ville.
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
     * Permet à l'utilisateur de sélectionner une ville parmi une liste.
     *
     * @param cities liste des villes disponibles.
     * @return la ville sélectionnée, ou null si aucune n'est choisie.
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

            City createdCity = services.getCityService().create(city);
            if (createdCity != null && createdCity.getId() != null) {
                return createdCity;
            } else {
                System.out.println("Erreur lors de la création de la ville !");
                return null;
            }
        }

        return services.getCityService().findByZipCode(choice);
    }

    /**
     * Permet à l'utilisateur de sélectionner un type de restaurant parmi une liste.
     *
     * @param types liste des types de restaurants disponibles.
     * @return le type sélectionné, ou null si aucun n'est choisi.
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
     * Recherche des restaurants par leur type.
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
     * Ajoute un nouveau restaurant en demandant les informations nécessaires à l'utilisateur.
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
        do {
            city = pickCity(services.getCityService().getAll());
        } while (city == null);

        RestaurantType restaurantType = null;
        do {
            restaurantType = pickRestaurantType(services.getRestaurantTypeService().getAll());
        } while (restaurantType == null);


        Restaurant restaurant = new Restaurant(null, name, description, website, street, city, restaurantType);

        restaurant = services.getRestaurantService().create(restaurant);

        if (restaurant != null && restaurant.getId() != null) {
            System.out.println("Restaurant créé avec succès !");
            showRestaurant(restaurant);
        } else {
            System.out.println("Erreur lors de la création du restaurant !");
        }
    }

    /**
     * Affiche les détails d'un restaurant et propose des actions à l'utilisateur.
     *
     * @param restaurant le restaurant à afficher.
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
        do { 
            showRestaurantMenu();
            choice = readInt();
            proceedRestaurantMenu(choice, restaurant);
        } while (choice != 0 && choice != 6); 
    }

    /**
     * Affiche les actions disponibles pour un restaurant sélectionné.
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
     * Gère le choix de l'utilisateur dans le menu d'un restaurant.
     *
     * @param choice numéro de l'action choisie.
     * @param restaurant le restaurant sur lequel l'action doit être réalisée.
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
     * Ajoute une évaluation basique (like ou dislike) à un restaurant.
     *
     * @param restaurant le restaurant évalué.
     * @param like indique s'il s'agit d'un like ou d'un dislike.
     */
    private static void addBasicEvaluation(Restaurant restaurant, Boolean like) {
        String ipAddress;
        try {
            ipAddress = Inet4Address.getLocalHost().toString(); 
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
     * Crée une évaluation complète pour un restaurant.
     *
     * @param restaurant le restaurant à évaluer.
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
            grade = new Grade(null, note, null, currentCriteria); 
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
     * Met à jour les informations d'un restaurant.
     * Gère les conflits optimistes en permettant à l'utilisateur de recharger et réessayer.
     *
     * @param restaurant le restaurant à modifier.
     */
    private static void editRestaurant(Restaurant restaurant) {
        boolean retry;
        do {
            retry = false;
            
            System.out.println("======================================================");
            System.out.println("Edition d'un restaurant !");
            System.out.println("======================================================");

            // Afficher les valeurs actuelles et demander les nouvelles
            System.out.println("Nom actuel : " + restaurant.getName());
            System.out.println("Nouveau nom (ou ENTER pour garder) : ");
            String newName = readString();
            if (!newName.trim().isEmpty()) {
                restaurant.setName(newName);
            }

            System.out.println("\nDescription actuelle : " + restaurant.getDescription());
            System.out.println("Nouvelle description (ou ENTER pour garder) : ");
            String newDesc = readString();
            if (!newDesc.trim().isEmpty()) {
                restaurant.setDescription(newDesc);
            }

            System.out.println("\nSite web actuel : " + restaurant.getWebsite());
            System.out.println("Nouveau site web (ou ENTER pour garder) : ");
            String newWebsite = readString();
            if (!newWebsite.trim().isEmpty()) {
                restaurant.setWebsite(newWebsite);
            }

            System.out.println("\nType actuel : " + restaurant.getType().getLabel());
            System.out.println("Changer le type de restaurant ? (O/n) ");
            String changeType = readString();
            if (changeType.equalsIgnoreCase("O")) {
                RestaurantType newType = pickRestaurantType(services.getRestaurantTypeService().getAll());
                if (newType != null && !newType.equals(restaurant.getType())) {
                    restaurant.setType(newType);
                }
            }

            // Tentative de sauvegarde
            boolean success = services.getRestaurantService().update(restaurant);

            if (success) {
                System.out.println("\n✅ Merci, le restaurant a bien été modifié !");
            } else {
                System.out.println("\n⚠️  ====== CONFLIT OPTIMISTE DÉTECTÉ ======");
                System.out.println("Le restaurant a été modifié par un autre utilisateur pendant votre édition.");
                System.out.println("Vos modifications ne peuvent pas être appliquées dans l'état actuel.");
                System.out.println("\nQue souhaitez-vous faire ?");
                System.out.println("1. Recharger les données actuelles et réessayer vos modifications");
                System.out.println("2. Abandonner les modifications");
                System.out.print("Votre choix : ");

                int choice = readInt();
                if (choice == 1) {
                    // Recharger le restaurant avec les données fraîches de la base
                    Restaurant freshRestaurant = services.getRestaurantService().findById(restaurant.getId());
                    if (freshRestaurant != null) {
                        System.out.println("\n📋 ATTENTION - Le restaurant a été modifié par quelqu'un d'autre :");
                        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                        
                        // Afficher les différences
                        if (!restaurant.getName().equals(freshRestaurant.getName())) {
                            System.out.println("  Nom : '" + restaurant.getName() + "' → '" + freshRestaurant.getName() + "'");
                        }
                        if (!restaurant.getDescription().equals(freshRestaurant.getDescription())) {
                            System.out.println("  Description : modifiée par un autre utilisateur");
                        }
                        if (!restaurant.getWebsite().equals(freshRestaurant.getWebsite())) {
                            System.out.println("  Site web : '" + restaurant.getWebsite() + "' → '" + freshRestaurant.getWebsite() + "'");
                        }
                        if (!restaurant.getType().equals(freshRestaurant.getType())) {
                            System.out.println("  Type : '" + restaurant.getType().getLabel() + "' → '" + freshRestaurant.getType().getLabel() + "'");
                        }
                        
                        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                        
                        // Remplacer par les données fraîches
                        restaurant = freshRestaurant;
                        retry = true;
                        
                        logger.warn("Conflit optimiste résolu - Utilisateur rechargera les données et réessayera");
                        System.out.println("\n🔄 Données rechargées. Vous pouvez maintenant réessayer vos modifications...\n");
                    } else {
                        System.out.println("❌ ERREUR : Le restaurant a été supprimé par un autre utilisateur !");
                        logger.error("Impossible de recharger le restaurant - Il a été supprimé");
                    }
                } else {
                    System.out.println("❌ Modifications annulées.");
                    logger.info("Utilisateur a annulé les modifications après un conflit optimiste");
                }
            }
        } while (retry);
    }

    /**
     * Met à jour l'adresse d'un restaurant.
     * Gère les conflits optimistes en permettant à l'utilisateur de recharger et réessayer.
     *
     * @param restaurant le restaurant dont l'adresse doit être mise à jour.
     */
    private static void editRestaurantAddress(Restaurant restaurant) {
        boolean retry;
        do {
            retry = false;
            
            System.out.println("======================================================");
            System.out.println("Edition de l'adresse d'un restaurant !");
            System.out.println("======================================================");

            System.out.println("Rue actuelle : " + restaurant.getAddress().getStreet());
            System.out.println("Nouvelle rue (ou ENTER pour garder) : ");
            String newStreet = readString();
            if (!newStreet.trim().isEmpty()) {
                restaurant.getAddress().setStreet(newStreet);
            }

            System.out.println("\nVille actuelle : " + restaurant.getAddress().getCity().getCityName() + 
                             " (" + restaurant.getAddress().getCity().getZipCode() + ")");
            System.out.println("Changer la ville ? (O/n) ");
            String changeCity = readString();
            
            if (changeCity.equalsIgnoreCase("O")) {
                City newCity = null;
                do { 
                    newCity = pickCity(services.getCityService().getAll());
                    if (newCity != null && newCity.getId() != null) {
                        restaurant.getAddress().setCity(newCity);
                    } else {
                        System.out.println("Ville invalide, veuillez réessayer.");
                    }
                } while (newCity == null || newCity.getId() == null);
            }

            // Tentative de sauvegarde
            boolean success = services.getRestaurantService().update(restaurant);

            if (success) {
                System.out.println("\n✅ L'adresse a bien été modifiée ! Merci !");
            } else {
                System.out.println("\n⚠️  ====== CONFLIT OPTIMISTE DÉTECTÉ ======");
                System.out.println("L'adresse du restaurant a été modifiée par un autre utilisateur pendant votre édition.");
                System.out.println("\nQue souhaitez-vous faire ?");
                System.out.println("1. Recharger les données actuelles et réessayer");
                System.out.println("2. Abandonner les modifications");
                System.out.print("Votre choix : ");

                int choice = readInt();
                if (choice == 1) {
                    Restaurant freshRestaurant = services.getRestaurantService().findById(restaurant.getId());
                    if (freshRestaurant != null) {
                        System.out.println("\n📋 ATTENTION - L'adresse a été modifiée par quelqu'un d'autre :");
                        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                        System.out.println("  Rue : " + freshRestaurant.getAddress().getStreet());
                        System.out.println("  Ville : " + freshRestaurant.getAddress().getCity().getCityName() + 
                                         " (" + freshRestaurant.getAddress().getCity().getZipCode() + ")");
                        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                        
                        restaurant = freshRestaurant;
                        retry = true;
                        
                        logger.warn("Conflit optimiste sur adresse - Utilisateur rechargera les données");
                        System.out.println("\n🔄 Données rechargées. Vous pouvez réessayer...\n");
                    } else {
                        System.out.println("❌ ERREUR : Le restaurant a été supprimé !");
                        logger.error("Restaurant supprimé pendant la modification d'adresse");
                    }
                } else {
                    System.out.println("❌ Modifications annulées.");
                    logger.info("Modifications d'adresse annulées après conflit");
                }
            }
        } while (retry);
    }

    /**
     * Supprime un restaurant après confirmation de l'utilisateur.
     * Gère les conflits optimistes si le restaurant a été modifié ou supprimé entre-temps.
     *
     * @param restaurant le restaurant à supprimer.
     */
    private static void deleteRestaurant(Restaurant restaurant) {
        System.out.println("Etes-vous sûr de vouloir supprimer ce restaurant ? (O/n)");
        String choice = readString();
        if (choice.equals("o") || choice.equals("O")) {
            boolean success = services.getRestaurantService().delete(restaurant);

            if (success) {
                System.out.println("✅ Le restaurant a bien été supprimé !");
                logger.info("Restaurant supprimé : id={}, nom={}", restaurant.getId(), restaurant.getName());
            } else {
                System.out.println("\n⚠️  IMPOSSIBLE DE SUPPRIMER LE RESTAURANT");
                System.out.println("Le restaurant a été modifié ou supprimé par un autre utilisateur.");
                System.out.println("Veuillez recharger la liste des restaurants.");
                logger.warn("Échec de suppression - Conflit optimiste pour restaurant id={}", restaurant.getId());
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
        do { 
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
