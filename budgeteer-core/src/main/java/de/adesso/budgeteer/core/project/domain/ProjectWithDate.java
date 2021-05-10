package de.adesso.budgeteer.core.project.domain;

import de.adesso.budgeteer.core.common.DateRange;
import lombok.Value;

@Value
public class ProjectWithDate {
    long id;
    String name;
    DateRange dateRange;
}
