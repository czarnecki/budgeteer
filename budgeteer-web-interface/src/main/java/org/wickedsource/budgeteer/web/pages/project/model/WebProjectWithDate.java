package org.wickedsource.budgeteer.web.pages.project.model;

import de.adesso.budgeteer.core.common.DateRange;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class WebProjectWithDate extends WebProject {
    private DateRange dateRange;

    public WebProjectWithDate(long id, String name, DateRange dateRange) {
        super(id, name);
        this.dateRange = dateRange;
    }
}
