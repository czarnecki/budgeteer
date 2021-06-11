package org.wickedsource.budgeteer.web.pages.contract.overview.report.form;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FormattedDate implements Serializable {
	private final String label;
	private final LocalDate date;
	
	public FormattedDate(LocalDate date, DateTimeFormatter format) {
		this.label = format.format(date);
		this.date = date;
	}

	public String getLabel() {
		return label;
	}

	public LocalDate getDate() {
		return date;
	}
}
