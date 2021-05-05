package de.adesso.budgeteer.core.contract.port.in;

import de.adesso.budgeteer.core.contract.domain.Contract;

public interface GetContractTemplateUseCase {
    Contract getContractTemplate(long projectId);
}
