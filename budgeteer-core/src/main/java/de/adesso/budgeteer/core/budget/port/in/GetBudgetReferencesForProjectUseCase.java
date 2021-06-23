package de.adesso.budgeteer.core.budget.port.in;

import de.adesso.budgeteer.core.budget.domain.BudgetReference;

import java.util.List;

public interface GetBudgetReferencesForProjectUseCase {
    List<BudgetReference> getBudgetReferencesForProject(long projectId);
}
