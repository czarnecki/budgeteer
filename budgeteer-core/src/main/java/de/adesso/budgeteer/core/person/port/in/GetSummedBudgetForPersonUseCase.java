package de.adesso.budgeteer.core.person.port.in;

import org.joda.money.Money;

public interface GetSummedBudgetForPersonUseCase {
    Money getSummedBudgetForPerson(long personId);
}
