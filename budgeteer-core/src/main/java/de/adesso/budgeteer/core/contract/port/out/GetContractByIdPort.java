package de.adesso.budgeteer.core.contract.port.out;

import de.adesso.budgeteer.core.contract.domain.Contract;

public interface GetContractByIdPort {
    Contract getContractById(long contractId);
}
