package org.wickedsource.budgeteer.persistence.template;

import de.adesso.budgeteer.core.template.domain.Template;
import de.adesso.budgeteer.core.template.port.out.GetTemplateByIdPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TemplateAdapter implements GetTemplateByIdPort {

    private final TemplateRepository templateRepository;
    private final TemplateMapper templateMapper;

    @Override
    public Template getTemplateById(long templateId) {
        return templateRepository.findById(templateId)
                .map(templateMapper::mapToDomain)
                .orElseThrow();
    }
}
