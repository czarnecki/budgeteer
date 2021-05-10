package de.adesso.budgeteer.core.common;

import lombok.Value;

import java.io.Serializable;
import java.util.Date;

@Value
public class DateRange implements Serializable {
    Date startDate;
    Date endDate;
}
