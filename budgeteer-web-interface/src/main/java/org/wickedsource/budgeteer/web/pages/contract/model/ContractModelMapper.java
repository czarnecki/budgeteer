package org.wickedsource.budgeteer.web.pages.contract.model;

import de.adesso.budgeteer.core.contract.domain.Contract;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContractModelMapper {
    public ContractModel mapToModel(Contract contract) {
        return new ContractModel(
                contract.getId(),
                contract.getProjectId(),
                contract.getInternalNumber(),
                contract.getName(),
                mapType(contract.getType()),
                contract.getStartDate(),
                contract.getBudget(),
                contract.getBudgetSpent(),
                contract.getBudgetLeft(),
                contract.getTaxRate(),
                contract.getAttributes()
        );
    }

    public ContractOverviewModel mapToModel(List<Contract> contracts) {
        return new ContractOverviewModel(contracts.stream().map(this::mapToModel).collect(Collectors.toList()));
    }

    private ContractModel.Type mapType(Contract.Type type) {
        if (type == Contract.Type.FIXED_PRICE) {
            return ContractModel.Type.FIXED_PRICE;
        }
        return ContractModel.Type.TIME_AND_MATERIAL;
    }
}
