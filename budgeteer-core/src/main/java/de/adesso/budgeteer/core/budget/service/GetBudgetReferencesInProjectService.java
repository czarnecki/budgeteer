package de.adesso.budgeteer.core.budget.service;

import de.adesso.budgeteer.core.budget.domain.BudgetReference;
import de.adesso.budgeteer.core.budget.port.in.GetBudgetReferencesForProjectUseCase;
import de.adesso.budgeteer.core.budget.port.out.GetBudgetReferencesForProjectPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetBudgetReferencesInProjectService implements GetBudgetReferencesForProjectUseCase {

    private final GetBudgetReferencesForProjectPort getBudgetReferencesForProject;

    @Override
    public List<BudgetReference> getBudgetReferencesForProject(long projectId) {
        return getBudgetReferencesForProject.getBudgetReferencesForProject(projectId);
    }
}
