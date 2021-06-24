package org.wickedsource.budgeteer.web.pages.budgets.components.weekreporttable;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wickedsource.budgeteer.web.BudgeteerSession;
import org.wickedsource.budgeteer.web.pages.budgets.models.BudgetTagFilterModel;
import org.wickedsource.budgeteer.service.record.AggregatedRecord;
import org.wickedsource.budgeteer.service.record.RecordService;

import java.util.List;

public class BudgetsWeeklyAggregatedRecordsModel extends LoadableDetachableModel<List<AggregatedRecord>> {

    @SpringBean
    private RecordService service;

    private long budgetId;

    private IModel<BudgetTagFilterModel> filterModel;

    public BudgetsWeeklyAggregatedRecordsModel(long budgetId) {
        Injector.get().inject(this);
        this.budgetId = budgetId;
    }

    public BudgetsWeeklyAggregatedRecordsModel(IModel<BudgetTagFilterModel> filterModel) {
        Injector.get().inject(this);
        this.filterModel = filterModel;
    }

    @Override
    protected List<AggregatedRecord> load() {
        if (budgetId != 0) {
            return service.getWeeklyAggregationForBudgetWithTax(budgetId);
        } else if (filterModel != null && filterModel.getObject() != null) {
            return service.getWeeklyAggregationForBudgetsWithTaxes(filterModel.getObject());
        } else {
            throw new IllegalStateException("Neither budgetId nor filter specified. Specify at least one of these attributes in the constructor!");
        }
    }
}
