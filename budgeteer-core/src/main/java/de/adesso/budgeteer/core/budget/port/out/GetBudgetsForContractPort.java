package de.adesso.budgeteer.core.budget.port.out;

import de.adesso.budgeteer.core.budget.domain.Budget;

import java.util.List;

public interface GetBudgetsForContractPort {
    List<Budget> getBudgetsForContract(long contractId);
}
