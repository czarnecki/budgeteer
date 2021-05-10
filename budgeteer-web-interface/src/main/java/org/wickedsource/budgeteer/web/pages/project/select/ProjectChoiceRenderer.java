package org.wickedsource.budgeteer.web.pages.project.select;

import de.adesso.budgeteer.core.project.domain.Project;
import org.wickedsource.budgeteer.web.pages.base.AbstractChoiceRenderer;
import org.wickedsource.budgeteer.web.pages.project.model.WebProject;

public class ProjectChoiceRenderer extends AbstractChoiceRenderer<WebProject> {
    @Override
    public Object getDisplayValue(WebProject object) {
        return object.getName();
    }
}
