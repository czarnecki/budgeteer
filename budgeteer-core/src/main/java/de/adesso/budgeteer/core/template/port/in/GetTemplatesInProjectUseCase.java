package de.adesso.budgeteer.core.template.port.in;

import de.adesso.budgeteer.core.template.domain.Template;

import java.util.List;

public interface GetTemplatesInProjectUseCase {
    List<Template> getTemplatesInProject(long projectId);
}
