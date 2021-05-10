package de.adesso.budgeteer.core.project.port.in;

import de.adesso.budgeteer.core.common.DateRange;
import lombok.Value;

public interface UpdateProjectUseCase {
    void updateProject(UpdateProjectCommand command);

    @Value
    class UpdateProjectCommand {
        long id;
        String name;
        DateRange dateRange;
    }
}
