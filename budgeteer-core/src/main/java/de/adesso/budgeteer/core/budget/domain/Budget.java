package de.adesso.budgeteer.core.budget.domain;

import lombok.Value;
import org.joda.money.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Value
public class Budget {
    long id;
    String name;
    String importKey;
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
    BigDecimal taxRate;

    public BigDecimal taxRateAsCoefficient() {
        return BigDecimal.ONE.add(taxRate.divide(BigDecimal.valueOf(100), RoundingMode.HALF_DOWN));
    }

    public Money getRemaining() {
        return total.minus(spent);
    }
}
