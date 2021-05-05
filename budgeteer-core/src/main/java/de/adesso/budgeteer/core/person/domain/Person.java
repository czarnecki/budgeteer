package de.adesso.budgeteer.core.person.domain;

import lombok.Value;
import org.joda.money.Money;

import java.util.Date;

@Value
public class Person {
    long id;
    String name;
    Date lastBooked;
    Money averageDailyRate;
    Money defaultDailyRate;
}
