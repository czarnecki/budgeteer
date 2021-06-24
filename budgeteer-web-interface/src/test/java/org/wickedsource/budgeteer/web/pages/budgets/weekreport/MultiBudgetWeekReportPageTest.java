package org.wickedsource.budgeteer.web.pages.budgets.weekreport;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.jupiter.api.Test;
import org.wickedsource.budgeteer.web.pages.budgets.models.BudgetTagFilterModel;
import org.wickedsource.budgeteer.web.AbstractWebTestTemplate;
import org.wickedsource.budgeteer.web.BudgeteerSession;
import org.wickedsource.budgeteer.web.pages.budgets.weekreport.multi.MultiBudgetWeekReportPage;

import java.util.ArrayList;
import java.util.List;

public class MultiBudgetWeekReportPageTest extends AbstractWebTestTemplate {

    private static final List<String> EMPTY_STRING_LIST = new ArrayList<>(0);

    @Test
    void testSingleBudget() {
        WicketTester tester = getTester();
        BudgetTagFilterModel filter = new BudgetTagFilterModel(EMPTY_STRING_LIST);
        BudgeteerSession.get().setBudgetFilter(filter);
        tester.startPage(MultiBudgetWeekReportPage.class);
        tester.assertRenderedPage(MultiBudgetWeekReportPage.class);
    }

    @Override
    protected void setupTest() {

    }
}
