package de.adesso.budgeteer.core.contract.port.in;

import java.io.File;
import java.time.LocalDate;

public interface ExportContractUseCase {
    File exportContract(long projectId, long templateId, LocalDate endDate);
}
