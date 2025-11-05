package ch.hearc.ig.guideresto.presentation;

import ch.hearc.ig.guideresto.business.City;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

public class Application2 {

    public static void main(String[] args) {
        // Remplacez "guiderestoPU" par le nom de votre persistence-unit dans persistence.xml
        String persistenceUnitName = "guideRestoJPA";
        EntityManagerFactory emf = null;
        EntityManager em = null;

        try {
            emf = Persistence.createEntityManagerFactory(persistenceUnitName);
            em = emf.createEntityManager();

            System.out.println("EntityManager démarré: " + (em != null && em.isOpen()));

            // Exemple: lister toutes les villes existantes
            List<City> cities = em.createQuery("SELECT c FROM City c", City.class).getResultList();
            System.out.println("Villes trouvées: " + cities.size());
            for (City c : cities) {
                System.out.println(" - id=" + c.getId() + " / npa=" + c.getZipCode() + " / nom=" + c.getCityName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) em.close();
            if (emf != null && emf.isOpen()) emf.close();
            System.out.println("EntityManager fermé.");
        }
    }
}