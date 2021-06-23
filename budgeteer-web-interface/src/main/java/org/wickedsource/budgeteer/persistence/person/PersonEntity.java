package org.wickedsource.budgeteer.persistence.person;

import lombok.Data;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.wickedsource.budgeteer.persistence.project.ProjectEntity;
import org.wickedsource.budgeteer.persistence.record.PlanRecordEntity;
import org.wickedsource.budgeteer.persistence.record.RecordEntity;
import org.wickedsource.budgeteer.persistence.record.WorkRecordEntity;

import javax.persistence.*;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "PERSON", indexes = {@Index(name = "PERSON_PROJECT_ID_IDX", columnList = "PROJECT_ID")})
public class PersonEntity {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 255)
    private String importKey;

    @Column
    private Money defaultDailyRate;

    @ManyToOne(optional = false)
    private ProjectEntity project;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "person", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<DailyRateEntity> dailyRates = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "person", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<WorkRecordEntity> workRecords = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "person", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PlanRecordEntity> planRecords = new ArrayList<>();

    public Money averageDailyRate() {
        return getWorkRecords().stream()
                .map(WorkRecordEntity::getActualRate)
                .reduce(Money.of(CurrencyUnit.EUR, 0), Money::plus)
                .dividedBy(getWorkRecords().size(), RoundingMode.HALF_DOWN);
    }

    public Date lastBooked() {
        return getWorkRecords().stream()
                .map(RecordEntity::getDate)
                .min(Date::compareTo)
                .orElse(null);
    }
}
