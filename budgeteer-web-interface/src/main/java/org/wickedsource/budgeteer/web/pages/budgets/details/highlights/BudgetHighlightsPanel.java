package org.wickedsource.budgeteer.web.pages.budgets.details.highlights;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.wickedsource.budgeteer.web.components.money.MoneyLabel;
import org.wickedsource.budgeteer.web.components.nullmodel.NullsafeModel;
import org.wickedsource.budgeteer.web.components.percent.PercentageLabel;
import org.wickedsource.budgeteer.web.pages.budgets.models.BudgetModel;

public class BudgetHighlightsPanel extends Panel {

    private static final String FALLBACK_STRING = new StringResourceModel("nullString").getString();

    public BudgetHighlightsPanel(String id, IModel<BudgetModel> model) {
        super(id, model);
        add(new Label("name", model.map(BudgetModel::getName)));
        add(new Label("contract", model.map(BudgetModel::getContractName).orElse(FALLBACK_STRING)));
        add(new Label("description", model.map(BudgetModel::getDescription).orElse(FALLBACK_STRING)));
        add(new MoneyLabel("total", model.map(BudgetModel::getTotal), true));
        add(new MoneyLabel("remaining", model.map(BudgetModel::getRemaining), true));
        add(new MoneyLabel("spent", model.map(BudgetModel::getSpent), true));
        add(new MoneyLabel("limit", model.map(BudgetModel::getLimit), true));
        add(new PercentageLabel("progress", model.map(BudgetModel::getProgress)));
        add(new MoneyLabel("avgDailyRate", model.map(BudgetModel::getAverageDailyRate), true));
        add(new Label("lastUpdated", model.map(BudgetModel::getLastUpdated)));
    }
}
