package org.wickedsource.budgeteer.web.pages.contract.budgetOverview;

import de.adesso.budgeteer.core.budget.port.in.GetBudgetsForContractUseCase;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wickedsource.budgeteer.web.Mount;
import org.wickedsource.budgeteer.web.pages.base.basepage.BasePage;
import org.wickedsource.budgeteer.web.pages.base.basepage.breadcrumbs.BreadcrumbsModel;
import org.wickedsource.budgeteer.web.pages.budgets.models.BudgetModelMapper;
import org.wickedsource.budgeteer.web.pages.budgets.overview.table.BudgetOverviewTable;
import org.wickedsource.budgeteer.web.pages.contract.details.ContractDetailsPage;
import org.wickedsource.budgeteer.web.pages.contract.overview.ContractOverviewPage;
import org.wickedsource.budgeteer.web.pages.dashboard.DashboardPage;

@Mount("/contracts/details/budgets/${id}")
public class BudgetForContractOverviewPage extends BasePage {

    @SpringBean
    private GetBudgetsForContractUseCase getBudgetsForContractUseCase;

    @SpringBean
    private BudgetModelMapper budgetModelMapper;

    public BudgetForContractOverviewPage(PageParameters pageParameters) {
        super(pageParameters);
        add(new BudgetOverviewTable("budgetTable", () -> budgetModelMapper.toBudgetModel(getBudgetsForContractUseCase.getBudgetsForContract(getParameterId()))));
    }

    @Override
    protected BreadcrumbsModel getBreadcrumbsModel() {
        BreadcrumbsModel model =  new BreadcrumbsModel(DashboardPage.class, ContractOverviewPage.class);
        model.addBreadcrumb(ContractDetailsPage.class, getPageParameters());
        model.addBreadcrumb(BudgetForContractOverviewPage.class, getPageParameters());
        return model;
    }
}
