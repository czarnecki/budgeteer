package de.adesso.budgeteer.core.budget.port.out;

public interface BudgetImportKeyExistsInProjectPort {
    boolean budgetImportKeyExistsForProject(long projectId, String importKey);
}
