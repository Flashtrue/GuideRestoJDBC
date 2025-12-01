package ch.hearc.ig.guideresto.business;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.*;

/**
 * @author cedric.baudet
 */
@Entity
@Table(name = "CRITERES_EVALUATION")
@NamedQueries({
        @NamedQuery(
                name = "CompleteEvaluation.findAll",
                query = "SELECT c FROM CompleteEvaluation c ORDER BY c.visitDate DESC"
        ),
        @NamedQuery(
                name = "CompleteEvaluation.findByRestaurant",
                query = "SELECT c FROM CompleteEvaluation c WHERE c.restaurant = :restaurant"
        ),
        @NamedQuery(
                name = "CompleteEvaluation.findByUsername",
                query = "SELECT c FROM CompleteEvaluation c WHERE UPPER(c.username) LIKE UPPER(:username)"
        ),
        @NamedQuery(
                name = "CompleteEvaluation.deleteById",
                query = "DELETE FROM CompleteEvaluation c WHERE c.id = :id"
        )
})
public class EvaluationCriteria implements IBusinessObject {
    
    @Id
    @Column(name = "NUMERO")
    private Integer id;
    
    @Column(name = "NOM")
    private String name;
    
    @Column(name = "DESCRIPTION")
    private String description;

    public EvaluationCriteria() {
        this(null, null);
    }

    public EvaluationCriteria(String name, String description) {
        this(null, name, description);
    }

    public EvaluationCriteria(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}