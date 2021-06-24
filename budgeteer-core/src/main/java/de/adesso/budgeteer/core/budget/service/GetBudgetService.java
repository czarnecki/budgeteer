package de.adesso.budgeteer.core.budget.service;

import de.adesso.budgeteer.core.budget.domain.Budget;
import de.adesso.budgeteer.core.budget.port.in.GetBudgetUseCase;
import de.adesso.budgeteer.core.budget.port.out.GetBudgetPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetBudgetService implements GetBudgetUseCase {

    private final GetBudgetPort getBudgetPort;

    @Override
    public Budget getBudget(long budgetId) {
        return getBudgetPort.getBudget(budgetId);
    }
}
