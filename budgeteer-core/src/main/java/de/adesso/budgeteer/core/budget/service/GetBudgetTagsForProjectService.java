package de.adesso.budgeteer.core.budget.service;

import de.adesso.budgeteer.core.budget.port.in.GetBudgetTagsForProjectUseCase;
import de.adesso.budgeteer.core.budget.port.out.GetBudgetTagsForProjectPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetBudgetTagsForProjectService implements GetBudgetTagsForProjectUseCase {

    private final GetBudgetTagsForProjectPort getBudgetTagsForProjectPort;

    @Override
    public List<String> getBudgetTagsForProject(long projectId) {
        return getBudgetTagsForProjectPort.getBudgetTagsForProject(projectId);
    }
}
