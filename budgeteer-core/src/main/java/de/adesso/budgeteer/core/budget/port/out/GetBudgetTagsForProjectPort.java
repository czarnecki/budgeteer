package de.adesso.budgeteer.core.budget.port.out;

import java.util.List;

public interface GetBudgetTagsForProjectPort {
    List<String> getBudgetTagsForProject(long projectId);
}
