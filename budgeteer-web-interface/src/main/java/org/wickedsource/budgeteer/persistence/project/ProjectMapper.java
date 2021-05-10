package org.wickedsource.budgeteer.persistence.project;

import de.adesso.budgeteer.core.common.DateRange;
import de.adesso.budgeteer.core.project.domain.Project;
import de.adesso.budgeteer.core.project.domain.ProjectWithDate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProjectMapper {
    public Project mapToDomain(ProjectEntity projectEntity) {
        return new Project(projectEntity.getId(), projectEntity.getName());
    }

    public ProjectWithDate mapToProjectWithDate(ProjectEntity projectEntity) {
        return new ProjectWithDate(projectEntity.getId(), projectEntity.getName(), new DateRange(projectEntity.getProjectStart(), projectEntity.getProjectEnd()));
    }

    public List<Project> mapToDomain(List<ProjectEntity> projectEntities) {
        return projectEntities.stream().map(this::mapToDomain).collect(Collectors.toList());
    }
}
