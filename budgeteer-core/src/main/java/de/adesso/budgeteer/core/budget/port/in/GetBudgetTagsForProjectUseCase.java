package de.adesso.budgeteer.core.budget.port.in;

import java.util.List;

public interface GetBudgetTagsForProjectUseCase {
    List<String> getBudgetTagsForProject(long projectId);
}
