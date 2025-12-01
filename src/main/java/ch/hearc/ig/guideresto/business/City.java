package ch.hearc.ig.guideresto.business;

import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.*;

/**
 * @author cedric.baudet
 */
@Entity
@Table (name = "VILLES")
@NamedQueries({
        @NamedQuery(
                name = "City.findAll",
                query = "SELECT c FROM City c ORDER BY c.cityName"
        ),
        @NamedQuery(
                name = "City.findByZipCode",
                query = "SELECT c FROM City c WHERE c.zipCode = :zipCode"
        ),
        @NamedQuery(
                name = "City.findByCityName",
                query = "SELECT c FROM City c WHERE UPPER(c.cityName) LIKE UPPER(:cityName)"
        ),
        @NamedQuery(
                name = "City.deleteById",
                query = "DELETE FROM City c WHERE c.id = :id"
        )
})
public class City implements IBusinessObject {
    @Id
    @Column(name = "NUMERO")
    private Integer id;
    @Column (name = "CODE_POSTAL")
    private String zipCode;
    @Column (name = "NOM_VILLE")
    private String cityName;
    @Transient
    private Set<Restaurant> restaurants;

    public City() {
        this(null, null);
    }

    public City(String zipCode, String cityName) {
        this(null, zipCode, cityName);
    }

    public City(Integer id, String zipCode, String cityName) {
        this.id = id;
        this.zipCode = zipCode;
        this.cityName = cityName;
        this.restaurants = new HashSet();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String city) {
        this.cityName = city;
    }

    public Set<Restaurant> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(Set<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

}