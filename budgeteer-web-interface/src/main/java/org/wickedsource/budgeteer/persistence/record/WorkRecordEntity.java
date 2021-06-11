package org.wickedsource.budgeteer.persistence.record;

import org.joda.money.Money;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.math.RoundingMode;

@Entity
@Table(name = "WORK_RECORD", indexes = {
        @Index(name = "WORK_RECORD_BUDGET_ID_IDX", columnList = "BUDGET_ID"),
        @Index(name = "WORK_RECORD_PERSON_ID_IDX", columnList = "PERSON_ID")
})
public class WorkRecordEntity extends RecordEntity {

    @Column(name="EDITED_MANUALLY")
    private Boolean editedManually;

    public Boolean isEditedManually() {
        return editedManually;
    }

    public void setEditedManually(Boolean editedManually) {
        this.editedManually = editedManually;
    }

    public Money getBudgetBurned() {
        return getDailyRate().multipliedBy(getMinutes() / (60.0 * 8.0), RoundingMode.HALF_DOWN);
    }

    public double getHours() {
        return getMinutes() / 60.0;
    }

}
