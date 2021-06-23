package de.adesso.budgeteer.core.budget.port.out;

public interface BudgetNameExistsInProjectPort {
    boolean budgetNameExistsInProject(long projectId, String name);
}
