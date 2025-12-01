package ch.hearc.ig.guideresto.business;

import jakarta.persistence.*;

/**
 * @author cedric.baudet
 */
@Entity
@Table(name = "NOTES")
@NamedQueries({
        @NamedQuery(
                name = "Grade.findAll",
                query = "SELECT g FROM Grade g"
        ),
        @NamedQuery(
                name = "Grade.findByEvaluation",
                query = "SELECT g FROM Grade g WHERE g.evaluation = :evaluation"
        ),
        @NamedQuery(
                name = "Grade.findByCriteria",
                query = "SELECT g FROM Grade g WHERE g.criteria = :criteria"
        ),
        @NamedQuery(
                name = "Grade.findByGradeValue",
                query = "SELECT g FROM Grade g WHERE g.grade = :grade"
        ),
        @NamedQuery(
                name = "Grade.deleteById",
                query = "DELETE FROM Grade g WHERE g.id = :id"
        )
})
public class Grade implements IBusinessObject {

    @Id
    @Column(name = "NUMERO")
    private Integer id;

    @Column(name = "NOTE")
    private Integer grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_COMM")
    private CompleteEvaluation evaluation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_CRIT")
    private EvaluationCriteria criteria;

    public Grade() {
        this(null, null, null);
    }

    public Grade(Integer grade, CompleteEvaluation evaluation, EvaluationCriteria criteria) {
        this(null, grade, evaluation, criteria);
    }

    public Grade(Integer id, Integer grade, CompleteEvaluation evaluation, EvaluationCriteria criteria) {
        this.id = id;
        this.grade = grade;
        this.evaluation = evaluation;
        this.criteria = criteria;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public CompleteEvaluation getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(CompleteEvaluation evaluation) {
        this.evaluation = evaluation;
    }

    public EvaluationCriteria getCriteria() {
        return criteria;
    }

    public void setCriteria(EvaluationCriteria criteria) {
        this.criteria = criteria;
    }


}