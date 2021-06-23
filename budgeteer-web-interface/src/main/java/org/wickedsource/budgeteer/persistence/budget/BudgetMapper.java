package org.wickedsource.budgeteer.persistence.budget;

import de.adesso.budgeteer.core.budget.domain.Budget;
import de.adesso.budgeteer.core.budget.domain.BudgetSummary;
import de.adesso.budgeteer.core.common.DateRange;
import money.MoneyUtil;
import org.joda.money.CurrencyUnit;
import org.springframework.stereotype.Component;
import org.wickedsource.budgeteer.persistence.contract.ContractFieldEntity;
import org.wickedsource.budgeteer.persistence.record.RecordEntity;
import org.wickedsource.budgeteer.persistence.record.WorkRecordEntity;
import org.wickedsource.budgeteer.service.DateUtil;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BudgetMapper {
    public Budget mapToBudget(BudgetEntity budgetEntity) {
        return new Budget(budgetEntity.getId(),
                budgetEntity.getName(),
                budgetEntity.getImportKey(),
                budgetEntity.getDescription(),
                budgetEntity.getNote(),
                budgetEntity.getTags().stream().map(BudgetTagEntity::getTag).collect(Collectors.toList()),
                budgetEntity.getTotal(),
                MoneyUtil.sum(budgetEntity.getWorkRecords(), RecordEntity::getActualRate, CurrencyUnit.EUR),
                budgetEntity.getWorkRecords().stream().max(Comparator.comparing(RecordEntity::getDate)).map(RecordEntity::getActualRate).orElse(null),
                MoneyUtil.average(budgetEntity.getWorkRecords(), WorkRecordEntity::getActualRate, CurrencyUnit.EUR),
                MoneyUtil.sum(budgetEntity.getPlanRecords(), RecordEntity::getActualRate, CurrencyUnit.EUR),
                budgetEntity.getLimit(),
                budgetEntity.getContract().getId(),
                budgetEntity.getContract().getName(),
                budgetEntity.getContract().getTaxRate()
        );
    }

    public List<Budget> mapToBudget(List<BudgetEntity> budgetEntities) {
        return budgetEntities.stream().map(this::mapToBudget).collect(Collectors.toList());
    }

    public BudgetSummary mapToBudgetSummary(BudgetEntity budgetEntity, DateRange range) {
        var budget = mapToBudget(budgetEntity);
        var workRecordsInRange = budgetEntity.getWorkRecords().stream()
                .filter(workRecordEntity -> DateUtil.isDateInDateRange(workRecordEntity.getDate(), range))
                .collect(Collectors.toList());
        var spent = MoneyUtil.sum(workRecordsInRange, RecordEntity::getActualRate, CurrencyUnit.EUR);
        var remaining = budget.getTotal().minus(spent);
        var hours = workRecordsInRange.stream().mapToDouble(RecordEntity::getHours).sum();
        return new BudgetSummary(
                budget,
                range,
                mapAttributes(budgetEntity.getContract().getContractFields()),
                spent,
                remaining,
                hours
        );
    }

    public List<BudgetSummary> mapToBudgetSummary(List<BudgetEntity> budgetEntities, DateRange range) {
        return budgetEntities.stream().map(budgetEntity -> mapToBudgetSummary(budgetEntity, range)).collect(Collectors.toList());
    }

    private Map<String, String> mapAttributes(List<ContractFieldEntity> contractFieldEntities) {
        return contractFieldEntities.stream()
                .collect(Collectors.toMap(contractFieldEntity -> contractFieldEntity.getField().getFieldName(), ContractFieldEntity::getValue));
    }
}
