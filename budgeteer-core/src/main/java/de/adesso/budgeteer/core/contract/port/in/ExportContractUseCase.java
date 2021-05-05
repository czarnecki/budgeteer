package de.adesso.budgeteer.core.contract.port.in;

import java.io.File;
import java.util.Date;

public interface ExportContractUseCase {
    File exportContract(long projectId, long templateId, Date date);
}
