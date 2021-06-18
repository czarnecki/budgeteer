package org.wickedsource.budgeteer.persistence.person;

import de.adesso.budgeteer.core.common.DateRange;
import de.adesso.budgeteer.core.person.domain.Person;
import de.adesso.budgeteer.core.person.domain.PersonWithRate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PersonMapper {
    public Person mapToPerson(PersonEntity personEntity) {
        return new Person(
                personEntity.getId(),
                personEntity.getName(),
                personEntity.lastBooked(),
                personEntity.averageDailyRate(),
                personEntity.getDefaultDailyRate()
        );
    }

    public PersonWithRate mapToPersonWithRate(PersonEntity personEntity) {
        return new PersonWithRate(
                personEntity.getId(),
                personEntity.getName(),
                personEntity.lastBooked(),
                personEntity.averageDailyRate(),
                personEntity.getDefaultDailyRate(),
                mapRate(personEntity.getDailyRates())
        );
    }

    public List<Person> mapToDomain(List<PersonEntity> personEntities) {
        return personEntities.stream().map(this::mapToPerson).collect(Collectors.toList());
    }

    private PersonWithRate.Rate mapRate(DailyRateEntity dailyRateEntity) {
        return new PersonWithRate.Rate(
                dailyRateEntity.getPerson().getId(),
                dailyRateEntity.getBudget().getId(),
                dailyRateEntity.getRate(),
                new DateRange(dailyRateEntity.getDateStart(), dailyRateEntity.getDateEnd())
        );
    }

    private List<PersonWithRate.Rate> mapRate(List<DailyRateEntity> dailyRateEntities) {
        return dailyRateEntities.stream().map(this::mapRate).collect(Collectors.toList());
    }
}
