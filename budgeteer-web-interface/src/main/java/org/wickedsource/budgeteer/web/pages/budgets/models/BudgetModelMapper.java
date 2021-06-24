package org.wickedsource.budgeteer.web.pages.budgets.models;

import de.adesso.budgeteer.core.budget.domain.Budget;
import org.springframework.stereotype.Component;

@Component
public class BudgetModelMapper {
    public BudgetModel toBudgetModel(Budget budget) {
        return BudgetModel.builder()
                .id(budget.getId())
                .name(budget.getName())
                .importKey(budget.getImportKey())
                .description(budget.getDescription())
                .note(budget.getNote())
                .tags(budget.getTags())
                .total(budget.getTotal())
                .totalGross(budget.getTotalGross())
                .spent(budget.getSpent())
                .spentGross(budget.getSpentGross())
                .remaining(budget.getRemaining())
                .remainingGross(budget.getRemainingGross())
                .unplanned(budget.getUnplanned())
                .unplannedGross(budget.getUnplannedGross())
                .lastUpdated(budget.getLastUpdated())
                .averageDailyRate(budget.getAverageDailyRate())
                .limit(budget.getLimit())
                .contractId(budget.getContractId())
                .contractName(budget.getContractName())
                .build();
    }
}
