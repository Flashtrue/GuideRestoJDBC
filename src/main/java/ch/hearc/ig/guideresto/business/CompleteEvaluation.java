package ch.hearc.ig.guideresto.business;

/**
 * @author cedric.baudet
 */

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "COMMENTAIRES")
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
public class CompleteEvaluation extends Evaluation {

    @Lob
    @Column(name = "COMMENTAIRE")
    private String comment;

    @Column(name = "NOM_UTILISATEUR")
    private String username;

    @OneToMany(fetch = FetchType.LAZY)
    private Set<Grade> grades;

    public CompleteEvaluation() {
        this(null, null, null, null);
    }

    public CompleteEvaluation(Date visitDate, Restaurant restaurant, String comment, String username) {
        this(null, visitDate, restaurant, comment, username);
    }

    public CompleteEvaluation(Integer id, Date visitDate, Restaurant restaurant, String comment, String username) {
        super(id, visitDate, restaurant);
        this.comment = comment;
        this.username = username;
        this.grades = new HashSet();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<Grade> getGrades() {
        return grades;
    }

    public void setGrades(Set<Grade> grades) {
        this.grades = grades;
    }
}