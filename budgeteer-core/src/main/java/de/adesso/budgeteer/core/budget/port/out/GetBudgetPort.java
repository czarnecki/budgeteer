package de.adesso.budgeteer.core.budget.port.out;

import de.adesso.budgeteer.core.budget.domain.Budget;

public interface GetBudgetPort {
    Budget getBudget(long budgetId);
}
