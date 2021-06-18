package org.wickedsource.budgeteer.web.pages.person.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.joda.money.Money;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
public class PersonModel implements Serializable {
    private long id;
    private String name;
    private Date lastBooked;
    private Money averageDailyRate;
    private Money defaultDailyRate;
}
