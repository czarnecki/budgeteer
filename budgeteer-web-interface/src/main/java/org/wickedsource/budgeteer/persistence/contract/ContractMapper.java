package org.wickedsource.budgeteer.persistence.contract;

import de.adesso.budgeteer.core.contract.domain.Contract;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.stereotype.Component;
import org.wickedsource.budgeteer.persistence.budget.BudgetEntity;
import org.wickedsource.budgeteer.persistence.record.WorkRecordEntity;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContractMapper {
    public Contract mapToDomain(ContractEntity contractEntity) {
        var budgetSpent = contractEntity.getBudgets().stream()
                .map(BudgetEntity::getWorkRecords)
                .flatMap(List::stream)
                .map(WorkRecordEntity::getBudgetBurned)
                .reduce(Money.of(CurrencyUnit.EUR, 0), Money::plus);
        var budgetLeft = contractEntity.getBudget().minus(budgetSpent);
        var contractAttributes = contractEntity.getContractFields()
                .stream()
                .collect(Collectors.toMap(attribute -> attribute.getField().getFieldName(), ContractFieldEntity::getValue));
        return new Contract(
                contractEntity.getId(),
                contractEntity.getProject().getId(),
                contractEntity.getInternalNumber(),
                contractEntity.getName(),
                mapType(contractEntity.getType()),
                contractEntity.getStartDate(),
                contractEntity.getBudget(),
                budgetSpent,
                budgetLeft,
                contractEntity.getTaxRate(),
                contractAttributes
        );
    }

    public List<Contract> mapToDomain(List<ContractEntity> contractEntities) {
        return contractEntities.stream().map(this::mapToDomain).collect(Collectors.toList());
    }

    private Contract.Type mapType(ContractEntity.ContractType type) {
        if (type == ContractEntity.ContractType.FIXED_PRICE) {
            return Contract.Type.FIXED_PRICE;
        }
        return Contract.Type.TIME_AND_MATERIAL;
    }
}
