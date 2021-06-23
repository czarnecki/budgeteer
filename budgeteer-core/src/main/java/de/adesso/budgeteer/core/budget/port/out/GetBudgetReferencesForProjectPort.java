package de.adesso.budgeteer.core.budget.port.out;

import de.adesso.budgeteer.core.budget.domain.BudgetReference;

import java.util.List;

public interface GetBudgetReferencesForProjectPort {
    List<BudgetReference> getBudgetReferencesForProject(long projectId);
}
