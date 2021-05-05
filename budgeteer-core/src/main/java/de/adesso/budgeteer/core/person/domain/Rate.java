package de.adesso.budgeteer.core.person.domain;

import de.adesso.budgeteer.core.common.DateRange;
import lombok.Value;
import org.joda.money.Money;

@Value
public class Rate {
    long personId;
    long budgetId;
    Money amount;
    DateRange dateRange;
}
