package org.wickedsource.budgeteer.web.pages.contract.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.joda.money.Money;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Map;

@Data
@AllArgsConstructor
public class ContractModel implements Serializable {
    private long id;
    private long projectId;
    private String internalNumber;
    private String name;
    private Type type;
    private Date startDate;
    private Money budget;
    private Money budgetSpent;
    private Money budgetLeft;
    private BigDecimal taxRate;
    private Map<String, String> attributes;

    public Money getBudgetGross() {
        return applyTax(budget);
    }

    public Money getBudgetSpentGross() {
        return applyTax(budgetSpent);
    }

    public Money getBudgetLeftGross() {
        return applyTax(budgetLeft);
    }

    public BigDecimal getTaxCoefficient() {
        return BigDecimal.ONE.add(taxRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_DOWN));
    }

    private Money applyTax(Money money) {
        return money.multipliedBy(getTaxCoefficient(), RoundingMode.HALF_DOWN);
    }

    public enum Type {
        TIME_AND_MATERIAL,
        FIXED_PRICE
    }
}
