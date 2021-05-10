package org.wickedsource.budgeteer.web.pages.administration;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.jupiter.api.Test;
import org.wickedsource.budgeteer.web.AbstractWebTestTemplate;
import org.wickedsource.budgeteer.web.pages.project.administration.ProjectAdministrationPage;

public class ProjectWithDateAdministrationPageTest extends AbstractWebTestTemplate {

    @Test
    void test() {
        WicketTester tester = getTester();
        tester.startPage(ProjectAdministrationPage.class);
        tester.assertRenderedPage(ProjectAdministrationPage.class);
    }

    @Override
    protected void setupTest() {

    }
}
