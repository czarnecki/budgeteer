package org.wickedsource.budgeteer.web.pages.budgets.overview.table;

import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.wickedsource.budgeteer.service.budget.BudgetDetailData;
import org.wickedsource.budgeteer.service.budget.BudgetTagFilter;
import org.wickedsource.budgeteer.web.ClassAwareWrappingModel;
import org.wickedsource.budgeteer.web.components.money.MoneyLabel;
import org.wickedsource.budgeteer.web.pages.budgets.details.BudgetDetailsPage;
import org.wickedsource.budgeteer.web.pages.budgets.overview.table.progressbar.ProgressBar;

import java.util.List;

import static org.wicketstuff.lazymodel.LazyModel.from;
import static org.wicketstuff.lazymodel.LazyModel.model;

public class BudgetOverviewTable extends Panel {

    public BudgetOverviewTable(String id, FilteredBudgetModel model) {
        super(id, model);
        add(createBudgetList("budgetList", model));

        IModel<BudgetDetailData> totalModel = new TotalBudgetDetailsModel(model);
        add(new Label("totalLastUpdated", model(from(totalModel).getLastUpdated())));
        add(new MoneyLabel("totalAmount", model(from(totalModel).getTotal())));
        add(new MoneyLabel("totalSpent", model(from(totalModel).getSpent())));
        add(new MoneyLabel("totalRemaining", model(from(totalModel).getRemaining())));
        add(new ProgressBar("totalProgressBar", model(from(totalModel).getProgressInPercent())));
    }

    @Override
    public void onEvent(IEvent<?> event) {
        super.onEvent(event);
        if (event.getPayload() instanceof BudgetTagFilter) {
            BudgetTagFilter filter = (BudgetTagFilter) event.getPayload();
            FilteredBudgetModel model = (FilteredBudgetModel) getDefaultModel();
            model.setFilter(model(from(filter)));
        }
    }

    private ListView<BudgetDetailData> createBudgetList(String id, IModel<List<BudgetDetailData>> model) {
        return new ListView<BudgetDetailData>(id, model) {
            @Override
            protected void populateItem(final ListItem<BudgetDetailData> item) {
                BookmarkablePageLink link = new BookmarkablePageLink("detailLink", BudgetDetailsPage.class, BudgetDetailsPage.createParameters(item.getModelObject().getId()));
                Label linkTitle = new Label("detailLinkTitle", model(from(item.getModel()).getName()));
                link.add(linkTitle);
                item.add(link);
                item.add(new Label("lastUpdated", model(from(item.getModel()).getLastUpdated())));
                item.add(new MoneyLabel("amount", model(from(item.getModel()).getTotal())));
                item.add(new MoneyLabel("spent", model(from(item.getModel()).getSpent())));
                item.add(new MoneyLabel("remaining", model(from(item.getModel()).getRemaining())));
                item.add(new ProgressBar("progressBar", model(from(item.getModel()).getProgressInPercent())));
            }

            @Override
            protected ListItem<BudgetDetailData> newItem(int index, IModel<BudgetDetailData> itemModel) {
                return super.newItem(index, new ClassAwareWrappingModel<BudgetDetailData>(itemModel, BudgetDetailData.class));
            }
        };
    }
}