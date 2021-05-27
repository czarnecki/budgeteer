package org.wickedsource.budgeteer.web.pages.contract.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.wickedsource.budgeteer.MoneyUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Data
@RequiredArgsConstructor
public class ContractOverviewModel implements Serializable {

    private final List<ContractModel> contracts;

    public Money getTotalBudget() {
        return sum(ContractModel::getBudget);
    }

    public Money getTotalBudgetGross() {
        return sum(ContractModel::getBudgetGross);
    }

    public Money getTotalSpent() {
        return sum(ContractModel::getBudgetSpent);
    }

    public Money getTotalSpentGross() {
        return sum(ContractModel::getBudgetSpentGross);
    }

    public Money getTotalLeft() {
        return sum(ContractModel::getBudgetLeft);
    }

    public Money getTotalLeftGross() {
        return sum(ContractModel::getBudgetLeftGross);
    }

    public List<String> attributeKeys() {
        if (contracts.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(contracts.get(0).getAttributes().keySet());
    }

    public int attributeCount() {
        if (contracts.isEmpty()) {
            return 0;
        }
        return contracts.get(0).getAttributes().size();
    }

    private Money sum(Function<ContractModel, Money> mapper) {
        return MoneyUtil.sum(contracts, mapper, Money.of(CurrencyUnit.EUR, 0));
    }
}
