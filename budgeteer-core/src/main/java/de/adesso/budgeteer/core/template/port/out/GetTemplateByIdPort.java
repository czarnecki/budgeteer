package de.adesso.budgeteer.core.template.port.out;

import de.adesso.budgeteer.core.template.domain.Template;

public interface GetTemplateByIdPort {
    Template getTemplateById(long templateId);
}
