package de.adesso.budgeteer.core.budget.port.in;

import de.adesso.budgeteer.core.common.DateRange;

import java.io.File;

public interface ExportBudgetsUseCase {
    File exportContract(long projectId, long templateId, DateRange overall, DateRange month);
}
