package de.adesso.budgeteer.core.budget.domain;

import lombok.Value;
import org.joda.money.Money;

import java.util.List;

@Value
public class Budget {
    long id;
    String name;
    String description;
    String note;
    List<String> tags;
    Money total;
    Money spent;
    Money lastUpdated;
    Money averageDailyRate;
    Money unplanned;
    Money limit;
    long contractId;
    String contractName;

    public Money getRemaining() {
        return total.minus(spent);
    }
}
