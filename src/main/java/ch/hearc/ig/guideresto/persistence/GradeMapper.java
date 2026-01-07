package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.Grade;
import ch.hearc.ig.guideresto.business.CompleteEvaluation;
import ch.hearc.ig.guideresto.business.EvaluationCriteria;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class GradeMapper extends AbstractMapper<Grade> {

    /**
     * Constructeur par défaut initialisant le mapper pour les notes.
     */
    public GradeMapper() {
        super(Grade.class);
    }

    /**
     * Récupère toutes les notes.
     *
     * @return un ensemble de toutes les notes.
     */
    @Override
    public Set<Grade> findAll() {
        return new LinkedHashSet<>(em()
                .createNamedQuery("Grade.findAll", Grade.class)
                .getResultList());
    }

    /**
     * Récupère les notes associées à une évaluation complète donnée.
     *
     * @param evaluation l'évaluation complète pour laquelle récupérer les notes.
     * @return un ensemble des notes correspondantes, ou un ensemble vide si aucune évaluation n'est fournie.
     */
    public Set<Grade> findByEvaluation(CompleteEvaluation evaluation) {
        if (evaluation == null) {
            return Collections.emptySet();
        }
        return new LinkedHashSet<>(em()
                .createNamedQuery("Grade.findByEvaluation", Grade.class)
                .setParameter("evaluation", evaluation)
                .getResultList());
    }

    /**
     * Récupère les notes associées à un critère d'évaluation donné.
     *
     * @param criteria le critère d'évaluation pour lequel récupérer les notes.
     * @return un ensemble des notes correspondantes, ou un ensemble vide si aucun critère n'est fourni.
     */
    public Set<Grade> findByCriteria(EvaluationCriteria criteria) {
        if (criteria == null) {
            return Collections.emptySet();
        }
        return new LinkedHashSet<>(em()
                .createNamedQuery("Grade.findByCriteria", Grade.class)
                .setParameter("criteria", criteria)
                .getResultList());
    }

    /**
     * Récupère les notes correspondant à une valeur donnée.
     *
     * @param gradeValue la valeur de la note recherchée.
     * @return un ensemble des notes correspondantes, ou un ensemble vide si aucune valeur n'est fournie.
     */
    public Set<Grade> findByGradeValue(Integer gradeValue) {
        if (gradeValue == null) {
            return Collections.emptySet();
        }
        return new LinkedHashSet<>(em()
                .createNamedQuery("Grade.findByGradeValue", Grade.class)
                .setParameter("grade", gradeValue)
                .getResultList());
    }

    /**
     * Récupère les notes associées à l'identifiant d'une évaluation donnée.
     *
     * @param evaluationId l'identifiant de l'évaluation pour laquelle récupérer les notes.
     * @return un ensemble des notes correspondantes, ou un ensemble vide si l'identifiant est invalide.
     */
    public Set<Grade> findByEvaluationId(int evaluationId) {
        if (evaluationId <= 0) {
            return Collections.emptySet();
        }
        return new LinkedHashSet<>(em()
                .createQuery("SELECT g FROM Grade g WHERE g.evaluation.id = :evaluationId", Grade.class)
                .setParameter("evaluationId", evaluationId)
                .getResultList());
    }
}
