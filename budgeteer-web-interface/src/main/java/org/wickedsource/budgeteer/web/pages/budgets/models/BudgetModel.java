package org.wickedsource.budgeteer.web.pages.budgets.models;

import lombok.Builder;
import lombok.Data;
import org.joda.money.Money;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class BudgetModel implements Serializable {
    private long id;
    private String name;
    private String importKey;
    private String description;
    private String note;
    private List<String> tags;
    private Money total;
    private Money totalGross;
    private Money spent;
    private Money spentGross;
    private Money lastUpdated;
    private Money remaining;
    private Money remainingGross;
    private Money unplanned;
    private Money unplannedGross;
    private Money averageDailyRate;
    private double progress;
    private Money limit;
    private long contractId;
    private String contractName;
}
