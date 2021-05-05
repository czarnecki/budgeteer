package de.adesso.budgeteer.core.person.port.in;

import de.adesso.budgeteer.core.common.DateRange;
import lombok.Value;
import org.joda.money.Money;

public interface AddDailyRateUseCase {
    void addDailyRate();

    @Value
    class AddDailyRateCommand {
        long budgetId;
        Money amount;
        DateRange dateRange;
    }
}
