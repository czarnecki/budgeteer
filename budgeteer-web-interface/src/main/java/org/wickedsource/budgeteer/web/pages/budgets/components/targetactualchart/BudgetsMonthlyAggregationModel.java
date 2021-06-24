package org.wickedsource.budgeteer.web.pages.budgets.components.targetactualchart;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IObjectClassAwareModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wickedsource.budgeteer.web.pages.budgets.models.BudgetTagFilterModel;
import org.wickedsource.budgeteer.service.statistics.StatisticsService;
import org.wickedsource.budgeteer.service.statistics.TargetAndActual;

public class BudgetsMonthlyAggregationModel extends LoadableDetachableModel<TargetAndActual> implements IObjectClassAwareModel<TargetAndActual> {

    @SpringBean
    private StatisticsService service;

    private long budgetId;

    private IModel<BudgetTagFilterModel> filterModel;

    public BudgetsMonthlyAggregationModel(long budgetId) {
        Injector.get().inject(this);
        this.budgetId = budgetId;
    }

    public BudgetsMonthlyAggregationModel(IModel<BudgetTagFilterModel> filterModel) {
        this.filterModel = filterModel;
        Injector.get().inject(this);
    }

    @Override
    protected TargetAndActual load() {
        if (budgetId != 0) {
            return service.getMonthStatsForBudgetWithTax(budgetId, 12);
        } else if (filterModel != null && filterModel.getObject() != null) {
            return service.getMonthStatsForBudgetsWithTax(filterModel.getObject(), 12);
        } else {
            throw new IllegalStateException("Neither budgetId nor filter specified. Specify at least one of these attributes in the constructor!");
        }
    }

    @Override
    public Class<TargetAndActual> getObjectClass() {
        return TargetAndActual.class;
    }
}
