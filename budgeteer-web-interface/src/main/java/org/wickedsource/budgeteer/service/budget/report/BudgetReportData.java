package org.wickedsource.budgeteer.service.budget.report;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
class BudgetReportData {
	private long id;
	private Date from;
	private Date until;
	private String name;
	private double spent_net;
	private double spent_gross;
	private double hoursAggregated;
	private double budgetRemaining_net;
	private double budgetRemaining_gross;
	private Double progress;
	private Map<String, String> attributes;
}
