package org.wickedsource.budgeteer.persistence.budget;

import de.adesso.budgeteer.core.budget.domain.Budget;
import de.adesso.budgeteer.core.budget.domain.BudgetReference;
import de.adesso.budgeteer.core.budget.port.out.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.wickedsource.budgeteer.persistence.contract.ContractRepository;
import org.wickedsource.budgeteer.persistence.project.ProjectRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BudgetAdapter implements
        GetBudgetsInProjectPort,
        GetBudgetReferencesForProjectPort,
        CreateBudgetEntityPort,
        BudgetImportKeyExistsInProjectPort,
        BudgetNameExistsInProjectPort {

    private final BudgetRepository budgetRepository;
    private final ProjectRepository projectRepository;
    private final ContractRepository contractRepository;
    private final BudgetMapper budgetMapper;

    @Override
    @Transactional
    public List<Budget> getBudgetsInProject(long projectId) {
        var budgetEntities = budgetRepository.findByProjectIdOrderByNameAsc(projectId);
        return budgetMapper.mapToBudget(budgetEntities);
    }

    @Override
    public List<BudgetReference> getBudgetReferencesForProject(long projectId) {
        return budgetRepository.getAllByProjectId(projectId);
    }

    @Override
    @Transactional
    public void createBudgetEntity(CreateBudgetEntityCommand command) {
        var budgetEntity = new BudgetEntity();
        budgetEntity.setProject(projectRepository.findById(command.getProjectId()).orElseThrow());
        budgetEntity.setContract(command.getContractId().map(contractRepository::findById).orElseThrow().orElse(null));
        budgetEntity.setName(command.getName());
        budgetEntity.setDescription(command.getDescription());
        budgetEntity.setImportKey(command.getImportKey());
        budgetEntity.setTotal(command.getTotal());
        budgetEntity.setLimit(command.getLimit());
        budgetEntity.setTags(mapToTagEntities(command.getTags(), budgetEntity));
    }

    @Override
    public boolean budgetImportKeyExistsForProject(long projectId, String importKey) {
        return budgetRepository.existsByImportKeyAndProjectId(importKey, projectId);
    }

    @Override
    public boolean budgetNameExistsInProject(long projectId, String name) {
        return budgetRepository.existsByNameAndProjectId(name, projectId);
    }

    private List<BudgetTagEntity> mapToTagEntities(List<String> tags, BudgetEntity budgetEntity) {
        return tags.stream().map(tag -> new BudgetTagEntity(tag, budgetEntity)).collect(Collectors.toList());
    }
}
