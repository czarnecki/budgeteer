package de.adesso.budgeteer.core.person.port.out;

import de.adesso.budgeteer.core.person.domain.PersonWithRate;

public interface GetPersonWithRatePort {
    PersonWithRate getPersonWithRate(long personId);
}
