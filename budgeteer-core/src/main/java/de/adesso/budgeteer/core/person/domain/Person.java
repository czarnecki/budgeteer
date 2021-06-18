package de.adesso.budgeteer.core.person.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joda.money.Money;

import java.util.Date;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class Person {
    private final long id;
    private final String name;
    private final Date lastBooked;
    private final Money averageDailyRate;
    private final Money defaultDailyRate;
}
