package de.adesso.budgeteer.core.budget.port.in;

import de.adesso.budgeteer.core.budget.domain.Budget;

public interface GetBudgetUseCase {
    Budget getBudget(long budgetId);
}
