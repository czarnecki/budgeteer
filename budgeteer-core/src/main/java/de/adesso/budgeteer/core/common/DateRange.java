package de.adesso.budgeteer.core.common;

import lombok.Value;

import java.util.Date;

@Value
public class DateRange {
    Date startDate;
    Date endDate;
}
