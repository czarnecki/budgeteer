package de.adesso.budgeteer.core.person.service;

import de.adesso.budgeteer.core.person.domain.Person;
import de.adesso.budgeteer.core.person.port.in.GetPersonsInProjectUseCase;
import de.adesso.budgeteer.core.person.port.out.GetPersonsInProjectPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetPersonsInProjectService implements GetPersonsInProjectUseCase {

    private final GetPersonsInProjectPort getPersonsInProjectPort;

    @Override
    public List<Person> getPersonsInProject(long projectId) {
        return getPersonsInProjectPort.getPersonsInProject(projectId);
    }
}
