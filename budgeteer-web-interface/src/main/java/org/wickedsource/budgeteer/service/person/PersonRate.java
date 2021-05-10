package org.wickedsource.budgeteer.service.person;

import de.adesso.budgeteer.core.common.DateRange;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.joda.money.Money;
import org.wickedsource.budgeteer.service.budget.BudgetBaseData;

import java.io.Serializable;

@Data
@NoArgsConstructor(access = AccessLevel.NONE)
@AllArgsConstructor
@Accessors(chain = true)
public class PersonRate implements Serializable {
    private Money rate;
    private BudgetBaseData budget;
    private DateRange dateRange;
}
