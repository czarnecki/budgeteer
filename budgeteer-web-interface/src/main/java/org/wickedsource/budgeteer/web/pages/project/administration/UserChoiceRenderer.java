package org.wickedsource.budgeteer.web.pages.project.administration;

import org.wickedsource.budgeteer.web.pages.base.AbstractChoiceRenderer;

public class UserChoiceRenderer extends AbstractChoiceRenderer<WebUser> {

    @Override
    public Object getDisplayValue(WebUser object) {
        return object.getName();
    }
}
