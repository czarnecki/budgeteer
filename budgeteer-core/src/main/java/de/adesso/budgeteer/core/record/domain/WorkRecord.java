package de.adesso.budgeteer.core.record.domain;

import lombok.Value;
import org.joda.money.Money;

import java.util.Date;

@Value
public class WorkRecord {
    long id;
    String budgetName;
    String personName;
    Date date;
    double hours;
    Money budgetBurned;
    Money dailyRate;
    boolean editedManually;
}
