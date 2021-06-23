package org.wickedsource.budgeteer.persistence.budget;

import de.adesso.budgeteer.core.budget.domain.Budget;
import money.MoneyUtil;
import org.joda.money.CurrencyUnit;
import org.springframework.stereotype.Component;
import org.wickedsource.budgeteer.persistence.record.RecordEntity;
import org.wickedsource.budgeteer.persistence.record.WorkRecordEntity;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BudgetMapper {
    public Budget mapToBudget(BudgetEntity budgetEntity) {
        return new Budget(budgetEntity.getId(),
                budgetEntity.getName(),
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
                budgetEntity.getContract().getName()
        );
    }

    public List<Budget> mapToBudget(List<BudgetEntity> budgetEntities) {
        return budgetEntities.stream().map(this::mapToBudget).collect(Collectors.toList());
    }
}
