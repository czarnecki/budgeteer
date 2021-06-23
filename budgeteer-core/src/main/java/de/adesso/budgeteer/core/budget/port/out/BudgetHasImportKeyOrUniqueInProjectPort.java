package de.adesso.budgeteer.core.budget.port.out;

public interface BudgetHasImportKeyOrUniqueInProjectPort {
    boolean budgetHasImportKeyOrUniqueInProject(long budgetId, String importKey);
}
