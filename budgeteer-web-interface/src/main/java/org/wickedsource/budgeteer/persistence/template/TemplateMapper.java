package org.wickedsource.budgeteer.persistence.template;

import de.adesso.budgeteer.core.template.domain.Template;
import org.springframework.stereotype.Component;
import org.wickedsource.budgeteer.service.ReportType;

@Component
public class TemplateMapper {
    public Template mapToDomain(TemplateEntity templateEntity) {
        return new Template(
                templateEntity.getId(),
                templateEntity.getProjectId(),
                templateEntity.getName(),
                templateEntity.getDescription(),
                templateEntity.getType() == ReportType.BUDGET_REPORT ? Template.Type.BUDGET : Template.Type.CONTRACT,
                templateEntity.isDefault(),
                templateEntity.getWb()
        );
    }
}
