package org.wickedsource.budgeteer.web.pages.person.overview.table;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wickedsource.budgeteer.web.components.dataTable.DataTableBehavior;
import org.wickedsource.budgeteer.web.components.datelabel.DateLabel;
import org.wickedsource.budgeteer.web.components.money.MoneyLabel;
import org.wickedsource.budgeteer.web.pages.base.basepage.BasePage;
import org.wickedsource.budgeteer.web.pages.person.details.PersonDetailsPage;
import org.wickedsource.budgeteer.web.pages.person.edit.EditPersonPage;
import org.wickedsource.budgeteer.web.pages.person.models.PersonModel;
import org.wickedsource.budgeteer.web.pages.person.overview.PeopleOverviewPage;

import java.util.List;


public class PeopleOverviewTable extends Panel {

    public PeopleOverviewTable(String id, IModel<List<PersonModel>> model) {
        super(id, model);
        setRenderBodyOnly(true);
        var table = new WebMarkupContainer("table");
        table.add(new DataTableBehavior(DataTableBehavior.getRecommendedOptions()));

        table.add(createPersonList(model));
        add(table);
    }

    private ListView<PersonModel> createPersonList(IModel<List<PersonModel>> model) {
        return new ListView<>("personList", model) {
            @Override
            protected void populateItem(ListItem<PersonModel> item) {
                final PageParameters parameters = BasePage.createParameters(item.getModelObject().getId());
                var link = new BookmarkablePageLink<PersonDetailsPage>("personLink", PersonDetailsPage.class, parameters);
                link.add(new Label("personName", item.getModel().map(PersonModel::getName)));
                item.add(link);
                item.add(new MoneyLabel("dailyRate", item.getModel().map(PersonModel::getAverageDailyRate)));
                var defaultDailyRateLabel = item.getModelObject().getDefaultDailyRate() == null
                        ? new Label("defaultDailyRate", getString("nullString"))
                        : new MoneyLabel("defaultDailyRate", item.getModel().map(PersonModel::getDefaultDailyRate));
                item.add(defaultDailyRateLabel);
                item.add(new DateLabel("lastBookedDate", item.getModel().map(PersonModel::getLastBooked), true));

                var editPersonLink = new Link<>("editPage") {
                    @Override
                    public void onClick() {
                        WebPage page = new EditPersonPage(EditPersonPage.createParameters(item.getModelObject().getId()), PeopleOverviewPage.class, new PageParameters());
                        setResponsePage(page);
                    }
                };
                item.add(editPersonLink);

            }
        };
    }

}
