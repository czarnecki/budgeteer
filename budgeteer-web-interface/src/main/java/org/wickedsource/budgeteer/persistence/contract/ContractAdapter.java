package org.wickedsource.budgeteer.persistence.contract;

import de.adesso.budgeteer.core.contract.domain.Contract;
import de.adesso.budgeteer.core.contract.port.out.GetContractsInProjectPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ContractAdapter implements GetContractsInProjectPort {

    private final ContractRepository contractRepository;
    private final ContractMapper contractMapper;

    @Override
    @Transactional
    public List<Contract> getContractsInProject(long projectId) {
        return contractMapper.mapToDomain(contractRepository.findByProjectId(projectId));
    }
}
