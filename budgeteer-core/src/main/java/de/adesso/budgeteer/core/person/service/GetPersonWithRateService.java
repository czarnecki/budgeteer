package de.adesso.budgeteer.core.person.service;

import de.adesso.budgeteer.core.person.domain.PersonWithRate;
import de.adesso.budgeteer.core.person.port.in.GetPersonWithRateUseCase;
import de.adesso.budgeteer.core.person.port.out.GetPersonWithRatePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetPersonWithRateService implements GetPersonWithRateUseCase {

    private final GetPersonWithRatePort getPersonWithRatePort;

    @Override
    public PersonWithRate getPersonWithRate(long personId) {
        return getPersonWithRatePort.getPersonWithRate(personId);
    }
}
