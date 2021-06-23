package org.wickedsource.budgeteer.persistence.budget;

import de.adesso.budgeteer.core.budget.domain.Budget;
import de.adesso.budgeteer.core.budget.port.out.GetBudgetsInProjectPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BudgetAdapter implements GetBudgetsInProjectPort {

    private final BudgetRepository budgetRepository;
    private final BudgetMapper budgetMapper;

    @Override
    @Transactional
    public List<Budget> getBudgetsInProject(long projectId) {
        var budgetEntities = budgetRepository.findByProjectIdOrderByNameAsc(projectId);
        return budgetMapper.mapToBudget(budgetEntities);
    }
}
