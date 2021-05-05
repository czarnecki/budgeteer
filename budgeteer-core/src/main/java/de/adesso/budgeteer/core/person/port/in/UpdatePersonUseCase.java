package de.adesso.budgeteer.core.person.port.in;

import lombok.Value;
import org.joda.money.Money;

public interface UpdatePersonUseCase {
    void updatePerson();

    @Value
    class UpdatePersonCommand {
        String name;
        String internalNumber;
        Money defaultDailyRate;
    }
}
