package de.adesso.budgeteer.core.template.port.in;

import de.adesso.budgeteer.core.template.domain.Template;
import lombok.Value;

import java.io.File;

public interface UploadTemplateUseCase {
    void uploadTemplate();

    @Value
    class UploadTemplateCommand {
        long projectId;
        String name;
        String description;
        Template.Type type;
        File file;
    }
}
