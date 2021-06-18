package org.wickedsource.budgeteer.web.pages.person.models;

import de.adesso.budgeteer.core.person.domain.Person;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PersonModelMapper {
    public PersonModel toPersonModel(Person person) {
        return new PersonModel(
                person.getId(),
                person.getName(),
                person.getLastBooked(),
                person.getAverageDailyRate(),
                person.getDefaultDailyRate()
        );
    }

    public List<PersonModel> toPersonModel(List<Person> persons) {
        return persons.stream().map(this::toPersonModel).collect(Collectors.toList());
    }
}
