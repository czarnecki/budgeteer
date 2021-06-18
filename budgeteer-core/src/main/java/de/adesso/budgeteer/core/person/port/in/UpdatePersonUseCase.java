package de.adesso.budgeteer.core.person.port.in;

import de.adesso.budgeteer.core.person.domain.PersonWithRate;
import lombok.Value;
import org.joda.money.Money;

import java.util.List;

public interface UpdatePersonUseCase {
    void updatePerson(UpdatePersonCommand command);

    @Value
    class UpdatePersonCommand {
        long id;
        String name;
        String importKey;
        Money defaultDailyRate;
        List<PersonWithRate.Rate> rates;
    }
}
