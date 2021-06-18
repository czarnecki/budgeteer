package org.wickedsource.budgeteer.persistence.person;

import de.adesso.budgeteer.core.person.domain.Person;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.stereotype.Component;
import org.wickedsource.budgeteer.persistence.record.RecordEntity;
import org.wickedsource.budgeteer.persistence.record.WorkRecordEntity;

import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PersonMapper {
    public Person mapToDomain(PersonEntity personEntity) {
        var averageDailyRate = personEntity.getWorkRecords().stream()
                .map(WorkRecordEntity::getBudgetBurned)
                .reduce(Money.of(CurrencyUnit.EUR, 0), Money::plus)
                .dividedBy(personEntity.getWorkRecords().size(), RoundingMode.HALF_DOWN);
        var lastBooked = personEntity.getWorkRecords().stream()
                .map(RecordEntity::getDate)
                .max(Date::compareTo)
                .orElse(null);
        return new Person(
                personEntity.getId(),
                personEntity.getName(),
                lastBooked,
                averageDailyRate,
                personEntity.getDefaultDailyRate()
        );
    }

    public List<Person> mapToDomain(List<PersonEntity> personEntities) {
        return personEntities.stream().map(this::mapToDomain).collect(Collectors.toList());
    }
}
