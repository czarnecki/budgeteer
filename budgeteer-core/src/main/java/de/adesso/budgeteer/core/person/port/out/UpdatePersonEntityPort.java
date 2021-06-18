package de.adesso.budgeteer.core.person.port.out;

import de.adesso.budgeteer.core.person.domain.PersonWithRate;
import lombok.Value;
import org.joda.money.Money;

import java.util.List;

public interface UpdatePersonEntityPort {
    void updatePersonEntity(UpdatePersonEntityCommand command);

    @Value
    class UpdatePersonEntityCommand {
        long id;
        String name;
        String importKey;
        Money defaultDailyRate;
        List<PersonWithRate.Rate> rates;
    }
}
