package de.adesso.budgeteer.core.person.domain;

import de.adesso.budgeteer.core.common.DateRange;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;
import org.joda.money.Money;

import java.util.Date;
import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = true)
public class PersonWithRate extends Person {
    private final List<Rate> rates;

    public PersonWithRate(long id, String name, Date lastBooked, Money averageDailyRate, Money defaultDailyRate, List<Rate> rates) {
        super(id, name, lastBooked, averageDailyRate, defaultDailyRate);
        this.rates = rates;
    }

    @Value
    public static class Rate {
        long personId;
        long budgetId;
        Money amount;
        DateRange dateRange;
    }
}
