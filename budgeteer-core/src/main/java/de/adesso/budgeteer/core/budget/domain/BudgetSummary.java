package de.adesso.budgeteer.core.budget.domain;

import de.adesso.budgeteer.core.common.DateRange;
import lombok.Value;
import org.joda.money.Money;

import java.math.RoundingMode;
import java.util.Map;

@Value
public class BudgetSummary {
    Budget budget;
    DateRange range;
    Map<String, String> attributes;
    Money spent;
    Money budgetRemaining;
    double hoursAggregated;

    public Money getSpentGross() {
        return spent.multipliedBy(budget.taxRateAsCoefficient(), RoundingMode.HALF_DOWN);
    }

    public Money getBudgetRemainingGross() {
        return getBudgetRemaining().multipliedBy(budget.taxRateAsCoefficient(), RoundingMode.HALF_DOWN);
    }

    public Double getProgress() {
        var totalAmount = budget.getTotal().getAmount().doubleValue();
        var spentAmount = spent.getAmount().doubleValue();
        return Math.abs(totalAmount) < Math.ulp(1.0) && Math.abs(spentAmount) < Math.ulp(1.0) ? null : spentAmount / totalAmount;
    }
}
