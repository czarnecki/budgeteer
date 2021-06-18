package org.wickedsource.budgeteer.web.pages.person.overview;

import de.adesso.budgeteer.core.person.port.in.GetPersonsInProjectUseCase;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wickedsource.budgeteer.web.BudgeteerSession;
import org.wickedsource.budgeteer.web.Mount;
import org.wickedsource.budgeteer.web.pages.base.basepage.BasePage;
import org.wickedsource.budgeteer.web.pages.base.basepage.breadcrumbs.BreadcrumbsModel;
import org.wickedsource.budgeteer.web.pages.dashboard.DashboardPage;
import org.wickedsource.budgeteer.web.pages.person.models.PersonModelMapper;
import org.wickedsource.budgeteer.web.pages.person.overview.table.PeopleOverviewTable;

@Mount({"people"})
public class PeopleOverviewPage extends BasePage {

    @SpringBean
    private GetPersonsInProjectUseCase getPersonsInProjectUseCase;

    @SpringBean
    private PersonModelMapper personModelMapper;

    public PeopleOverviewPage() {
        var model = Model.ofList(personModelMapper.toPersonModel(getPersonsInProjectUseCase.getPersonsInProject(BudgeteerSession.get().getProjectId())));
        var table = new PeopleOverviewTable("peopleOverviewTable", model);
        add(table);
    }

    @Override
    protected BreadcrumbsModel getBreadcrumbsModel() {
        return new BreadcrumbsModel(DashboardPage.class, PeopleOverviewPage.class);
    }

}
