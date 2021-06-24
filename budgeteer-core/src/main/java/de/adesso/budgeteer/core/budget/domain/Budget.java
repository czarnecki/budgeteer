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

    public Money getTotalGross() {
        return applyTax(total);
    }

    public Money getSpentGross() {
        return applyTax(spent);
    }

    public Money getRemaining() {
        return total.minus(spent);
    }

    public Money getRemainingGross() {
        return applyTax(getRemaining());
    }

    public Money getUnplannedGross() {
        return applyTax(unplanned);
    }

    public BigDecimal taxRateAsCoefficient() {
        return BigDecimal.ONE.add(taxRate.divide(BigDecimal.valueOf(100), RoundingMode.HALF_DOWN));
    }

    public double getProgress() {
        return spent.getAmount().doubleValue() / total.getAmount().doubleValue();
    }

    private Money applyTax(Money money) {
        return money.multipliedBy(taxRateAsCoefficient(), RoundingMode.HALF_DOWN);
    }
}
