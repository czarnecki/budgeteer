package org.wickedsource.budgeteer.web.pages.budgets.overview.table;

import de.adesso.budgeteer.common.money.MoneyUtil;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.money.CurrencyUnit;
import org.wickedsource.budgeteer.service.budget.BudgetService;
import org.wickedsource.budgeteer.web.BudgeteerSession;
import org.wickedsource.budgeteer.web.ClassAwareWrappingModel;
import org.wickedsource.budgeteer.web.components.dataTable.DataTableBehavior;
import org.wickedsource.budgeteer.web.components.money.BudgetUnitMoneyModel;
import org.wickedsource.budgeteer.web.components.money.MoneyLabel;
import org.wickedsource.budgeteer.web.components.tax.TaxBudgetUnitMoneyModel;
import org.wickedsource.budgeteer.web.components.tax.TaxLabelModel;
import org.wickedsource.budgeteer.web.pages.base.basepage.BasePage;
import org.wickedsource.budgeteer.web.pages.base.delete.DeleteDialog;
import org.wickedsource.budgeteer.web.pages.budgets.details.BudgetDetailsPage;
import org.wickedsource.budgeteer.web.pages.budgets.edit.EditBudgetPage;
import org.wickedsource.budgeteer.web.pages.budgets.models.BudgetModel;
import org.wickedsource.budgeteer.web.pages.budgets.models.BudgetTagFilterModel;
import org.wickedsource.budgeteer.web.pages.budgets.overview.BudgetsOverviewPage;
import org.wickedsource.budgeteer.web.pages.budgets.overview.table.progressbar.ProgressBar;
import org.wickedsource.budgeteer.web.pages.contract.details.ContractDetailsPage;

import java.util.Comparator;
import java.util.List;

public class BudgetOverviewTable extends Panel {

    @SpringBean
    private BudgetService budgetService;

    public BudgetOverviewTable(String id, IModel<List<BudgetModel>> model) {
        super(id, model);
        var table = new WebMarkupContainer("table");
        table.add(new DataTableBehavior(DataTableBehavior.getRecommendedOptions()));

        createNetGrossOverviewLabels(table);

        table.add(createBudgetList(model));
        addTableSummaryLabels(table, model);

        add(table);
    }

    private void addTableSummaryLabels(WebMarkupContainer table, IModel<List<BudgetModel>> model) {
        table.add(new Label("totalLastUpdated", model.map(budgetModels -> budgetModels.stream().max(Comparator.comparing(BudgetModel::getLastUpdated)).map(BudgetModel::getLastUpdated).orElse(null))));
        var totalSpent = model.map(budgetModels -> MoneyUtil.sum(budgetModels, BudgetModel::getSpent, CurrencyUnit.EUR));
        var totalAmount = model.map(budgetModels -> MoneyUtil.sum(budgetModels, BudgetModel::getTotal, CurrencyUnit.EUR));
        table.add(new MoneyLabel("totalAmount",
                new TaxBudgetUnitMoneyModel(
                        new BudgetUnitMoneyModel(totalAmount),
                        new BudgetUnitMoneyModel(model.map(budgetModels -> MoneyUtil.sum(budgetModels, BudgetModel::getTotalGross, CurrencyUnit.EUR)))
                )));
        table.add(new MoneyLabel("totalSpent",
                new TaxBudgetUnitMoneyModel(
                        new BudgetUnitMoneyModel(totalSpent),
                        new BudgetUnitMoneyModel(model.map(budgetModels -> MoneyUtil.sum(budgetModels, BudgetModel::getSpentGross, CurrencyUnit.EUR)))
                )));
        table.add(new MoneyLabel("totalRemaining",
                new TaxBudgetUnitMoneyModel(
                        new BudgetUnitMoneyModel(model.map(budgetModels -> MoneyUtil.sum(budgetModels, BudgetModel::getRemaining, CurrencyUnit.EUR))),
                        new BudgetUnitMoneyModel(model.map(budgetModels -> MoneyUtil.sum(budgetModels, BudgetModel::getRemainingGross, CurrencyUnit.EUR)))
                )));
        table.add(new MoneyLabel("totalUnplanned",
                new TaxBudgetUnitMoneyModel(
                        new BudgetUnitMoneyModel(model.map(budgetModels -> MoneyUtil.sum(budgetModels, BudgetModel::getUnplanned, CurrencyUnit.EUR))),
                        new BudgetUnitMoneyModel(model.map(budgetModels -> MoneyUtil.sum(budgetModels, BudgetModel::getUnplannedGross, CurrencyUnit.EUR)))
                )));

        table.add(new ProgressBar("totalProgressBar", totalAmount.combineWith(totalSpent, (amount, spent) -> spent.getAmount().doubleValue() * 100.0 / amount.getAmount().doubleValue())));
    }

    @Override
    public void onEvent(IEvent<?> event) {
        super.onEvent(event);
        var payload = event.getPayload();
        if (payload instanceof BudgetTagFilterModel) {
            IModel<BudgetTagFilterModel> filter = () -> (BudgetTagFilterModel) payload;
            FilteredBudgetModel model = (FilteredBudgetModel) getDefaultModel();
            model.setTagFilter(filter);
        } else if (payload instanceof String) {
            long remainingFilter;
            try {
                remainingFilter = Long.parseLong((String) event.getPayload());
            } catch (NumberFormatException e) {
                return;
            }
            FilteredBudgetModel model = (FilteredBudgetModel) getDefaultModel();
            model.setRemainingFilter(() -> remainingFilter);
            BudgeteerSession.get().setRemainingBudetFilterValue(remainingFilter);
        }
    }

    private ListView<BudgetModel> createBudgetList(IModel<List<BudgetModel>> model) {
        return new ListView<>("budgetList", model) {
            @Override
            protected void populateItem(final ListItem<BudgetModel> item) {
                var link = new BookmarkablePageLink<>("detailLink", BudgetDetailsPage.class, BudgetDetailsPage.createParameters(item.getModelObject().getId()));
                var linkTitle = new Label("detailLinkTitle", item.getModel().map(BudgetModel::getName));
                link.add(linkTitle);
                item.add(link);
                item.add(new Label("lastUpdated", item.getModel().map(BudgetModel::getLastUpdated)));
                var contractLink = new Link<>("contractLink") {
                    @Override
                    public void onClick() {
                        setResponsePage(ContractDetailsPage.class, BasePage.createParameters(item.getModelObject().getContractId()));
                    }
                };
                var contractLinkTitle = new Label("contractTitle", item.getModel().map(BudgetModel::getContractName));
                contractLink.add(contractLinkTitle);
                item.add(contractLink);

                createBudgetListEntry(item);

                item.add(new ProgressBar("progressBar", item.getModel().map(BudgetModel::getProgress)));

                var deleteBudgetButton = new Link<>("deleteBudget") {
                    @Override
                    public void onClick() {
                        setResponsePage(new DeleteDialog() {
                            @Override
                            protected void onYes() {
                                budgetService.deleteBudget(item.getModelObject().getId());
                                setResponsePage(BudgetsOverviewPage.class);
                            }

                            @Override
                            protected void onNo() {
                                setResponsePage(BudgetsOverviewPage.class);
                            }

                            @Override
                            protected String confirmationText() {
                                return BudgetOverviewTable.this.getString("delete.budget.confirmation");
                            }
                        });
                    }
                };
                //Creating a separate tooltip is necessary because disabling the button also causes tooltips to disappear.
                var deleteBudgetTooltip = new WebMarkupContainer("deleteBudgetTooltip");
                if (item.getModelObject().getContractName() != null) {
                    deleteBudgetTooltip.add(new AttributeAppender("style", "cursor: not-allowed;", " "));
                    deleteBudgetTooltip.add(new AttributeModifier("title", getString("contract.still.exist")));
                    deleteBudgetButton.setEnabled(false);
                } else {
                    deleteBudgetTooltip.add(new AttributeModifier("title", getString("delete.budget")));
                }
                deleteBudgetTooltip.add(deleteBudgetButton);
                item.add(deleteBudgetTooltip);

                var editBudgetLink = new Link<>("editPage") {
                    @Override
                    public void onClick() {
                        WebPage page = new EditBudgetPage(EditBudgetPage.createParameters(
                                item.getModelObject().getId()), BudgetsOverviewPage.class, null, false);
                        setResponsePage(page);
                    }
                };
                item.add(editBudgetLink);
            }

            @Override
            protected ListItem<BudgetModel> newItem(int index, IModel<BudgetModel> itemModel) {
                return super.newItem(index, new ClassAwareWrappingModel<>(itemModel, BudgetModel.class));
            }
        };
    }

    private void createBudgetListEntry(ListItem<BudgetModel> item) {
        item.add(new MoneyLabel("amount",
                new TaxBudgetUnitMoneyModel(
                        new BudgetUnitMoneyModel(item.getModel().map(BudgetModel::getTotal)),
                        new BudgetUnitMoneyModel(item.getModel().map(BudgetModel::getTotalGross))
                )));

        var spentMoneyLabel = new MoneyLabel("spent",
                new TaxBudgetUnitMoneyModel(
                        new BudgetUnitMoneyModel(item.getModel().map(BudgetModel::getSpent)),
                        new BudgetUnitMoneyModel(item.getModel().map(BudgetModel::getSpentGross))
                ));
        if (item.getModelObject().getSpent().compareTo(item.getModelObject().getLimit()) >= 0 && !item.getModelObject().getLimit().isZero())
            spentMoneyLabel.add(new AttributeModifier("style", "color: red"));
        item.add(spentMoneyLabel);

        item.add(new MoneyLabel("remaining",
                new TaxBudgetUnitMoneyModel(
                        new BudgetUnitMoneyModel(item.getModel().map(BudgetModel::getRemaining)),
                        new BudgetUnitMoneyModel(item.getModel().map(BudgetModel::getRemainingGross))
                )));
        item.add(new MoneyLabel("unplanned",
                new TaxBudgetUnitMoneyModel(
                        new BudgetUnitMoneyModel(item.getModel().map(BudgetModel::getUnplanned)),
                        new BudgetUnitMoneyModel(item.getModel().map(BudgetModel::getUnplannedGross))
                )));
    }

    private void createNetGrossOverviewLabels(WebMarkupContainer table) {
        table.add(new Label("totalLabel", new TaxLabelModel(
                new StringResourceModel("overview.table.budget.totalLabel", this))));
        table.add(new Label("leftLabel", new TaxLabelModel(
                new StringResourceModel("overview.table.budget.leftLabel", this))));
        table.add(new Label("spentLabel", new TaxLabelModel(
                new StringResourceModel("overview.table.budget.spentLabel", this))));
        table.add(new Label("unplannedLabel", new TaxLabelModel(
                new StringResourceModel("overview.table.budget.unplannedLabel", this))));
    }
}
