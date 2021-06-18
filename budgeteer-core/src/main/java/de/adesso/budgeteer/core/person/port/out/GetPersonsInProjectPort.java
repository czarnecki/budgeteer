package de.adesso.budgeteer.core.person.port.out;

import de.adesso.budgeteer.core.person.domain.Person;

import java.util.List;

public interface GetPersonsInProjectPort {
    List<Person> getPersonsInProject(long projectId);
}
