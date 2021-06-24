package org.wickedsource.budgeteer.web.pages.budgets.overview;

import de.adesso.budgeteer.core.budget.port.in.GetBudgetTagsForProjectUseCase;
import de.adesso.budgeteer.core.budget.port.in.GetBudgetsInProjectUseCase;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wickedsource.budgeteer.service.budget.BudgetService;
import org.wickedsource.budgeteer.web.BudgeteerSession;
import org.wickedsource.budgeteer.web.Mount;
import org.wickedsource.budgeteer.web.components.links.NetGrossLink;
import org.wickedsource.budgeteer.web.pages.base.basepage.BasePage;
import org.wickedsource.budgeteer.web.pages.base.basepage.breadcrumbs.BreadcrumbsModel;
import org.wickedsource.budgeteer.web.pages.budgets.RemainingBudgetFilterModel;
import org.wickedsource.budgeteer.web.pages.budgets.edit.EditBudgetPage;
import org.wickedsource.budgeteer.web.pages.budgets.models.BudgetModelMapper;
import org.wickedsource.budgeteer.web.pages.budgets.models.BudgetTagFilterModel;
import org.wickedsource.budgeteer.web.pages.budgets.monthreport.multi.MultiBudgetMonthReportPage;
import org.wickedsource.budgeteer.web.pages.budgets.overview.filter.BudgetRemainingFilterPanel;
import org.wickedsource.budgeteer.web.pages.budgets.overview.filter.BudgetTagFilterPanel;
import org.wickedsource.budgeteer.web.pages.budgets.overview.report.BudgetReportPage;
import org.wickedsource.budgeteer.web.pages.budgets.overview.table.BudgetOverviewTable;
import org.wickedsource.budgeteer.web.pages.budgets.overview.table.FilteredBudgetModel;
import org.wickedsource.budgeteer.web.pages.budgets.weekreport.multi.MultiBudgetWeekReportPage;
import org.wickedsource.budgeteer.web.pages.dashboard.DashboardPage;

@Mount("budgets")
public class BudgetsOverviewPage extends BasePage {

    @SpringBean
    private BudgetService budgetService;

    @SpringBean
    private GetBudgetsInProjectUseCase getBudgetsInProjectUseCase;

    @SpringBean
    private GetBudgetTagsForProjectUseCase getBudgetTagsForProjectUseCase;

    @SpringBean
    private BudgetModelMapper budgetModelMapper;

    public BudgetsOverviewPage() {
        var tagsModel = Model.ofList(getBudgetTagsForProjectUseCase.getBudgetTagsForProject(BudgeteerSession.get().getProjectId()));
        if (BudgeteerSession.get().getBudgetFilter() == null) {
            var filter = new BudgetTagFilterModel(BudgeteerSession.get().getProjectId());
            BudgeteerSession.get().setBudgetFilter(filter);
        }
        add(new BudgetRemainingFilterPanel("remainingFilter", new RemainingBudgetFilterModel(BudgeteerSession.get().getProjectId())));
        add(new BudgetTagFilterPanel("tagFilter", tagsModel));
        var filteredBudgetModel = new FilteredBudgetModel(budgetModelMapper.toBudgetModel(getBudgetsInProjectUseCase.getBudgetsInProject(BudgeteerSession.get().getProjectId())), Model.of(BudgeteerSession.get().getBudgetFilter()));
        filteredBudgetModel.setRemainingFilter(() -> BudgeteerSession.get().getRemainingBudgetFilterValue());
        add(new BudgetOverviewTable("budgetTable", filteredBudgetModel));
        add(new BookmarkablePageLink<MultiBudgetWeekReportPage>("weekReportLink", MultiBudgetWeekReportPage.class));
        add(new BookmarkablePageLink<MultiBudgetMonthReportPage>("monthReportLink", MultiBudgetMonthReportPage.class));
        add(createNewBudgetLink());
        add(createReportLink());
        add(new NetGrossLink("netGrossLink"));
        add(createResetButton());
    }

    private Component createReportLink() {
        var link = new Link<>("createReportLink") {
			@Override
			public void onClick() {
				setResponsePage(new BudgetReportPage(BudgetsOverviewPage.class, new PageParameters()));
			}
		};

        if (!budgetService.projectHasBudgets(BudgeteerSession.get().getProjectId())) {
            link.setEnabled(false);
            link.add(new AttributeAppender("style", "cursor: not-allowed;", " "));
            link.add(new AttributeModifier("title", BudgetsOverviewPage.this.getString("links.budget.label.no.budget")));
        }
        return link;
	}

    private Link<Void> createNewBudgetLink() {
        return new Link<>("createBudgetLink") {
            @Override
            public void onClick() {
                WebPage page = new EditBudgetPage(BudgetsOverviewPage.class, getPageParameters());
                setResponsePage(page);
            }
        };
    }

    @Override
    protected BreadcrumbsModel getBreadcrumbsModel() {
        return new BreadcrumbsModel(DashboardPage.class, BudgetsOverviewPage.class);
    }

    private Link<Void> createResetButton() {
        return new Link<>("resetButton") {
            @Override
            public void onClick() {
                BudgeteerSession.get().getBudgetFilter().getSelectedTags().clear();
                BudgeteerSession.get().setRemainingBudetFilterValue(0L);
                setResponsePage(BudgetsOverviewPage.class);
            }
        };
    }
}
