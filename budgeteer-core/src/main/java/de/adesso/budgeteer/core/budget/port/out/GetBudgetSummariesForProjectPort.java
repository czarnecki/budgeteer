package de.adesso.budgeteer.core.budget.port.out;

import de.adesso.budgeteer.core.budget.domain.BudgetSummary;
import de.adesso.budgeteer.core.common.DateRange;

import java.util.List;

public interface GetBudgetSummariesForProjectPort {
    List<BudgetSummary> getBudgetSummariesForProject(long projectId, DateRange range);
}
