package de.adesso.budgeteer.core.person.port.in;

import de.adesso.budgeteer.core.person.domain.PersonWithRate;

public interface GetPersonWithRateUseCase {
    PersonWithRate getPersonWithRate(long personId);
}
