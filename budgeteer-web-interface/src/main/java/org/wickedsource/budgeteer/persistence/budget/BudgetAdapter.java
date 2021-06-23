package org.wickedsource.budgeteer.persistence.budget;

import de.adesso.budgeteer.core.budget.domain.Budget;
import de.adesso.budgeteer.core.budget.domain.BudgetReference;
import de.adesso.budgeteer.core.budget.port.out.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Delete;
import org.springframework.stereotype.Component;
import org.wickedsource.budgeteer.persistence.contract.ContractRepository;
import org.wickedsource.budgeteer.persistence.project.ProjectRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BudgetAdapter implements
        GetBudgetsInProjectPort,
        GetBudgetReferencesForProjectPort,
        CreateBudgetEntityPort,
        BudgetImportKeyExistsInProjectPort,
        BudgetNameExistsInProjectPort,
        UpdateBudgetEntityPort,
        BudgetHasImportKeyOrUniqueInProjectPort,
        BudgetHasNameOrUniqueInProjectPort,
        DeleteBudgetPort {

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

    @Override
    @Transactional
    public void updateBudgetEntity(UpdateBudgetEntityCommand command) {
        var budgetEntity = budgetRepository.findById(command.getBudgetId()).orElseThrow();
        var contractEntity = command.getContractId()
                .flatMap(contractRepository::findById)
                .orElse(null);
        budgetEntity.setContract(contractEntity);
        budgetEntity.setName(command.getName());
        budgetEntity.setDescription(command.getDescription());
        budgetEntity.setImportKey(command.getImportKey());
        budgetEntity.setTotal(command.getTotal());
        budgetEntity.setLimit(command.getLimit());
        budgetEntity.getTags().clear();
        budgetEntity.getTags().addAll(mapToTagEntities(command.getTags(), budgetEntity));
        budgetRepository.save(budgetEntity);
    }

    @Override
    public boolean budgetHasImportKeyOrUniqueInProject(long budgetId, String importKey) {
        return budgetRepository.existsByIdAndImportKey(budgetId, importKey)
                || !budgetRepository.importKeyExistsInProjectByBudgetId(budgetId, importKey);
    }

    @Override
    public boolean budgetHasNameOrUniqueInProject(long budgetId, String name) {
        return budgetRepository.existsByIdAndName(budgetId, name)
                || !budgetRepository.nameExistsInProjectByBudgetId(budgetId, name);
    }

    @Override
    public void deleteBudget(long budgetId) {
        budgetRepository.deleteById(budgetId);
    }

    private List<BudgetTagEntity> mapToTagEntities(List<String> tags, BudgetEntity budgetEntity) {
        return tags.stream().map(tag -> new BudgetTagEntity(tag, budgetEntity)).collect(Collectors.toList());
    }
}
