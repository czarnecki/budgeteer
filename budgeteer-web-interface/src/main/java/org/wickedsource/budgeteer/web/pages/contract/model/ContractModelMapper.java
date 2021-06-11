package org.wickedsource.budgeteer.web.pages.contract.model;

import de.adesso.budgeteer.core.contract.domain.Contract;
import org.springframework.stereotype.Component;
import org.wickedsource.budgeteer.web.components.fileUpload.FileUploadModel;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContractModelMapper {
    public ContractModel mapToModel(Contract contract) {
        var attributes = contract.getAttributes().entrySet().stream()
                .map(entry -> new ContractModel.Attribute(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

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
                attributes,
                new FileUploadModel(contract.getAttachment().getFileName(), contract.getAttachment().getFile(), contract.getAttachment().getLink())
        );
    }

    public List<ContractModel> mapToModel(List<Contract> contracts) {
        return contracts.stream().map(this::mapToModel).collect(Collectors.toList());
    }

    public ContractOverviewModel mapToOverviewModel(List<Contract> contracts) {
        return new ContractOverviewModel(contracts.stream().map(this::mapToModel).collect(Collectors.toList()));
    }

    private ContractModel.Type mapType(Contract.Type type) {
        if (type == Contract.Type.FIXED_PRICE) {
            return ContractModel.Type.FIXED_PRICE;
        }
        return ContractModel.Type.TIME_AND_MATERIAL;
    }
}
