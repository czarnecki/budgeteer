package de.adesso.budgeteer.core.budget.port.out;

public interface BudgetHasNameOrUniqueInProjectPort {
    boolean budgetHasNameOrUniqueInProject(long budgetId, String name);
}
