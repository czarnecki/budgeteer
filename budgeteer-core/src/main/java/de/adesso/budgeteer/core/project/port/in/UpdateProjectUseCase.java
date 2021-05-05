package de.adesso.budgeteer.core.project.port.in;

import lombok.Value;

import java.util.Date;

public interface UpdateProjectUseCase {
    void updateProject(UpdateProjectCommand command);

    @Value
    class UpdateProjectCommand {
        String name;
        Date dateRange;
    }
}
