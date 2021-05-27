package org.wickedsource.budgeteer.web.pages.contract.overview.table;

import de.adesso.budgeteer.core.contract.port.out.GetContractsInProjectPort;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.EnumLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wickedsource.budgeteer.MoneyUtil;
import org.wickedsource.budgeteer.web.BudgeteerSession;
import org.wickedsource.budgeteer.web.components.dataTable.DataTableBehavior;
import org.wickedsource.budgeteer.web.components.datelabel.DateLabel;
import org.wickedsource.budgeteer.web.components.money.BudgetUnitMoneyModel;
import org.wickedsource.budgeteer.web.components.money.MoneyLabel;
import org.wickedsource.budgeteer.web.components.tax.TaxBudgetUnitMoneyModel;
import org.wickedsource.budgeteer.web.components.tax.TaxLabelModel;
import org.wickedsource.budgeteer.web.pages.contract.details.ContractDetailsPage;
import org.wickedsource.budgeteer.web.pages.contract.edit.EditContractPage;
import org.wickedsource.budgeteer.web.pages.contract.model.ContractModel;
import org.wickedsource.budgeteer.web.pages.contract.model.ContractModelMapper;
import org.wickedsource.budgeteer.web.pages.contract.model.ContractOverviewModel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Map;

public class ContractOverviewTable extends Panel {

    @SpringBean
    private GetContractsInProjectPort getContractsInProjectPort;

    @SpringBean
    private ContractModelMapper contractModelMapper;

    public ContractOverviewTable(String id) {
        super(id);
        var model = Model.of(contractModelMapper.mapToModel(getContractsInProjectPort.getContractsInProject(BudgeteerSession.get().getProjectId())));
        var table = new WebMarkupContainer("table");

        createNetGrossLabels(table);
        table.add(new DataTableBehavior(DataTableBehavior.getRecommendedOptions()));
        table.add(new ListView<>("headerRow", model.map(ContractOverviewModel::attributeKeys)) {
            @Override
            protected void populateItem(ListItem<String> item) {
                item.add(new Label("headerItem", item.getModelObject()));
            }
        });
        table.add(new ListView<>("contractRows", model.map(ContractOverviewModel::getContracts)) {
            @Override
            protected void populateItem(ListItem<ContractModel> item) {
                long contractId = item.getModelObject().getId();

                var taxCoefficient = BudgeteerSession.get().isTaxEnabled() ? BigDecimal.ONE.add(item.getModelObject().getTaxRate().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_DOWN)) : BigDecimal.ONE;
                BookmarkablePageLink<EditContractPage> link = new BookmarkablePageLink<>("editContract",
                        ContractDetailsPage.class, EditContractPage.createParameters(contractId));
                link.add(new Label("contractName", item.getModel().map(ContractModel::getName)));
                item.add(link);
                item.add(new Label("internalNumber", item.getModel().map(ContractModel::getInternalNumber)));
                item.add(new DateLabel("startDate", item.getModel().map(ContractModel::getStartDate)));
                item.add(new EnumLabel<>("type", item.getModel().map(ContractModel::getType)));
                item.add(new ListView<>("contractRow", item.getModel().map(ContractModel::getAttributes).map(Map::values).map(ArrayList::new)) {
                    @Override
                    protected void populateItem(ListItem<String> item) {
                        item.add(new Label("contractRowText", item.getModel()));
                    }
                });
                item.add(new Label("budgetTotal", item.getModel().map(ContractModel::getBudget).map(budget -> MoneyUtil.toDouble(budget, BudgeteerSession.get().getSelectedBudgetUnit(), taxCoefficient.doubleValue()))));
                item.add(new Label("budgetSpent", item.getModel().map(ContractModel::getBudgetSpent).map(budgetSpent -> MoneyUtil.toDouble(budgetSpent, BudgeteerSession.get().getSelectedBudgetUnit(), taxCoefficient.doubleValue()))));
                item.add(new Label("budgetLeft", item.getModel().map(ContractModel::getBudgetLeft).map(budgetLeft -> MoneyUtil.toDouble(budgetLeft, BudgeteerSession.get().getSelectedBudgetUnit(), taxCoefficient.doubleValue()))));
                item.add(new BookmarkablePageLink<>("editLink", EditContractPage.class, EditContractPage.createParameters(contractId)));
            }
        });

        addTableSummaryLabels(table, model);
        add(table);
    }

    private void addTableSummaryLabels(WebMarkupContainer table, IModel<ContractOverviewModel> model) {
        // Fill up the columns which contain the contract attributes with empty cells
        var repeatingView = new RepeatingView("contractAttributeCell");
        for (var i = 0; i < model.getObject().attributeCount(); i++) {
            repeatingView.add(new Label(repeatingView.newChildId(), ""));
        }
        table.add(repeatingView);

        table.add(new MoneyLabel("totalAmount",
                new TaxBudgetUnitMoneyModel(
                        new BudgetUnitMoneyModel(model.map(ContractOverviewModel::getTotalBudget)),
                        new BudgetUnitMoneyModel(model.map(ContractOverviewModel::getTotalBudgetGross))
                )));
        table.add(new MoneyLabel("totalSpent",
                new TaxBudgetUnitMoneyModel(
                        new BudgetUnitMoneyModel(model.map(ContractOverviewModel::getTotalSpent)),
                        new BudgetUnitMoneyModel(model.map(ContractOverviewModel::getTotalSpentGross))
                )));
        table.add(new MoneyLabel("totalRemaining",
                new TaxBudgetUnitMoneyModel(
                        new BudgetUnitMoneyModel(model.map(ContractOverviewModel::getTotalLeft)),
                        new BudgetUnitMoneyModel(model.map(ContractOverviewModel::getTotalLeftGross))
                )));
    }


    private void createNetGrossLabels(WebMarkupContainer table) {
        table.add(new Label("totalLabel", new TaxLabelModel(
                new StringResourceModel("overview.table.contract.label.total", this))));
        table.add(new Label("leftLabel", new TaxLabelModel(
                new StringResourceModel("overview.table.contract.label.left", this))));
        table.add(new Label("spentLabel", new TaxLabelModel(
                new StringResourceModel("overview.table.contract.label.spent", this))));
    }
}
