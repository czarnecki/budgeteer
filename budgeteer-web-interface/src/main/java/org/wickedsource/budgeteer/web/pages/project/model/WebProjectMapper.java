package org.wickedsource.budgeteer.web.pages.project.model;

import de.adesso.budgeteer.core.project.domain.Project;
import de.adesso.budgeteer.core.project.domain.ProjectWithDate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class WebProjectMapper {
    public WebProject toWebProject(Project project) {
        return new WebProject(project.getId(), project.getName());
    }

    public WebProjectWithDate toWebProjectWithDate(ProjectWithDate project) {
        System.out.println("test");
        return new WebProjectWithDate(project.getId(), project.getName(), project.getDateRange());
    }

    public List<WebProject> toWebProject(List<Project> projects) {
        return projects.stream().map(this::toWebProject).collect(Collectors.toList());
    }
}
