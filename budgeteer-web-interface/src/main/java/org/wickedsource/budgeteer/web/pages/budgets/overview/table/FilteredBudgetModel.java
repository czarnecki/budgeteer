package org.wickedsource.budgeteer.web.pages.budgets.overview.table;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.joda.money.Money;
import org.wickedsource.budgeteer.web.pages.budgets.models.BudgetModel;
import org.wickedsource.budgeteer.web.pages.budgets.models.BudgetTagFilterModel;

import java.util.List;
import java.util.stream.Collectors;

public class FilteredBudgetModel extends LoadableDetachableModel<List<BudgetModel>> {

    private final List<BudgetModel> data;
    private IModel<BudgetTagFilterModel> tagFilter;
    private IModel<Long> remainingFilter = () -> 0L;

    public FilteredBudgetModel(List<BudgetModel> data, IModel<BudgetTagFilterModel> tagFilter) {
        this.data = data;
        this.tagFilter = tagFilter;
    }

    public void setTagFilter(IModel<BudgetTagFilterModel> tagFilter) {
        this.tagFilter = tagFilter;
    }

    public void setRemainingFilter(IModel<Long> remainingFilter) {
        this.remainingFilter = remainingFilter;
    }

    @Override
    protected List<BudgetModel> load() {
        return data.stream()
                .filter(this::filterTags)
                .filter(this::filterRemaining)
                .collect(Collectors.toList());
    }

    private boolean filterTags(BudgetModel budgetModel) {
        if (tagFilter.getObject().isEmpty()) {
            return true;
        }
        return budgetModel.getTags().stream().anyMatch(tag -> tagFilter.getObject().isTagSelected(tag));
    }

    private boolean filterRemaining(BudgetModel budgetModel) {
        return remainingFilter.getObject() == 0 || budgetModel.getRemaining().isGreaterThan(Money.of(budgetModel.getRemaining().getCurrencyUnit(), remainingFilter.getObject()));
    }
}
