package org.wickedsource.budgeteer.persistence.person;

import de.adesso.budgeteer.core.person.domain.Person;
import de.adesso.budgeteer.core.person.port.out.GetPersonsInProjectPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PersonAdapter implements GetPersonsInProjectPort {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    @Override
    @Transactional
    public List<Person> getPersonsInProject(long projectId) {
        return personMapper.mapToDomain(personRepository.findByProjectIdOrderByNameAsc(projectId));
    }
}
