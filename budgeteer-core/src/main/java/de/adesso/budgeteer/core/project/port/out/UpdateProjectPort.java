package de.adesso.budgeteer.core.project.port.out;

import de.adesso.budgeteer.core.common.DateRange;

public interface UpdateProjectPort {
    void updateProject(long projectId, String name, DateRange dateRange);
}
