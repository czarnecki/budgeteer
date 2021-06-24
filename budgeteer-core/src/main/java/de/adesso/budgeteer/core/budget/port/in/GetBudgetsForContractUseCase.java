package de.adesso.budgeteer.core.budget.port.in;

import de.adesso.budgeteer.core.budget.domain.Budget;

import java.util.List;

public interface GetBudgetsForContractUseCase {
    List<Budget> getBudgetsForContract(long contractId);
}
