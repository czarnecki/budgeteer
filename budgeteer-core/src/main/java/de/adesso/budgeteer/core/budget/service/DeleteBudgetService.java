package de.adesso.budgeteer.core.budget.service;

import de.adesso.budgeteer.core.budget.port.in.DeleteBudgetUseCase;
import de.adesso.budgeteer.core.budget.port.out.DeleteBudgetPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteBudgetService implements DeleteBudgetUseCase {

    private final DeleteBudgetPort deleteBudgetPort;

    @Override
    public void deleteBudget(long budgetId) {
        deleteBudgetPort.deleteBudget(budgetId);
    }
}
