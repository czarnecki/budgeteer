package de.adesso.budgeteer.core.project.port.in;

import de.adesso.budgeteer.core.project.domain.Project;
import lombok.Value;

public interface CreateProjectUseCase {
    Project createProject(CreateProjectCommand command);

    @Value
    class CreateProjectCommand {
        String name;
        long userId;
    }
}
