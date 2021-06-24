package de.adesso.budgeteer.core.budget.service;

import de.adesso.budgeteer.core.budget.domain.Budget;
import de.adesso.budgeteer.core.budget.port.in.GetBudgetsForContractUseCase;
import de.adesso.budgeteer.core.budget.port.out.GetBudgetsForContractPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetBudgetsForContractService implements GetBudgetsForContractUseCase {

    private final GetBudgetsForContractPort getBudgetsForContractPort;

    @Override
    public List<Budget> getBudgetsForContract(long contractId) {
        return getBudgetsForContractPort.getBudgetsForContract(contractId);
    }
}
