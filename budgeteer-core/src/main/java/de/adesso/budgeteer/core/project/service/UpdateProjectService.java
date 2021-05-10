package de.adesso.budgeteer.core.project.service;

import de.adesso.budgeteer.core.project.port.in.UpdateProjectUseCase;
import de.adesso.budgeteer.core.project.port.out.UpdateProjectPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateProjectService implements UpdateProjectUseCase {

    private final UpdateProjectPort updateProjectPort;

    @Override
    public void updateProject(UpdateProjectCommand command) {
        // TODO: Validation
        // name must be either the same or a unique new one
        // dateRange must not be null, and either both start and end date must be set or null
        // id must reference an existing project
        updateProjectPort.updateProject(command.getId(), command.getName(), command.getDateRange());
    }
}
