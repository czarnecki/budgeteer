package org.wickedsource.budgeteer.persistence.person;

import de.adesso.budgeteer.core.person.domain.Person;
import de.adesso.budgeteer.core.person.domain.PersonWithRate;
import de.adesso.budgeteer.core.person.port.out.GetPersonWithRatePort;
import de.adesso.budgeteer.core.person.port.out.GetPersonsInProjectPort;
import de.adesso.budgeteer.core.person.port.out.UpdatePersonEntityPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.wickedsource.budgeteer.persistence.budget.BudgetRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PersonAdapter implements
        GetPersonsInProjectPort,
        UpdatePersonEntityPort,
        GetPersonWithRatePort {

    private final PersonRepository personRepository;
    private final BudgetRepository budgetRepository;
    private final PersonMapper personMapper;

    @Override
    @Transactional
    public List<Person> getPersonsInProject(long projectId) {
        return personMapper.mapToDomain(personRepository.findByProjectIdOrderByNameAsc(projectId));
    }

    @Override
    @Transactional
    public void updatePersonEntity(UpdatePersonEntityCommand command) {
        var personEntity = personRepository.findById(command.getId()).orElseThrow();
        personEntity.setName(command.getName());
        personEntity.setImportKey(command.getImportKey());
        personEntity.setDefaultDailyRate(command.getDefaultDailyRate());
        /* For simplicity reasons we create a new list of DailyRateEntities that replace the existing ones
         * instead of updating existing ones and only create actually new entities. This isn't the most efficient
         * way, since all exiting objects are deleted and then new ones are inserted. This should be changed in the
         * future. */
        var newDailyRates = command.getRates().stream()
                .map(rate -> toDailyRateEntity(rate, personEntity))
                .collect(Collectors.toList());
        personEntity.getDailyRates().clear();
        personEntity.setDailyRates(newDailyRates);
        personRepository.save(personEntity);
    }

    @Override
    public PersonWithRate getPersonWithRate(long personId) {
        return personRepository.findById(personId).map(personMapper::mapToPersonWithRate).orElseThrow();
    }

    private DailyRateEntity toDailyRateEntity(PersonWithRate.Rate rate, PersonEntity personEntity) {
        var dailyRateEntity = new DailyRateEntity();
        dailyRateEntity.setRate(rate.getAmount());
        dailyRateEntity.setPerson(personEntity);
        dailyRateEntity.setBudget(budgetRepository.findById(rate.getBudgetId()).orElse(null));
        dailyRateEntity.setDateStart(rate.getDateRange().getStartDate());
        dailyRateEntity.setDateEnd(rate.getDateRange().getEndDate());
        return dailyRateEntity;
    }
}
