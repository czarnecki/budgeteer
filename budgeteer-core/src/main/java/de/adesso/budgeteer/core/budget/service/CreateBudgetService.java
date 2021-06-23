package de.adesso.budgeteer.core.budget.service;

import de.adesso.budgeteer.core.budget.port.in.CreateBudgetUseCase;
import de.adesso.budgeteer.core.budget.port.out.BudgetNameExistsInProjectPort;
import de.adesso.budgeteer.core.budget.port.out.CreateBudgetEntityPort;
import de.adesso.budgeteer.core.budget.port.out.BudgetImportKeyExistsInProjectPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateBudgetService implements CreateBudgetUseCase {

    private final CreateBudgetEntityPort createBudgetEntityPort;
    private final BudgetImportKeyExistsInProjectPort budgetImportKeyExistsInProjectPort;
    private final BudgetNameExistsInProjectPort budgetNameExistsInProjectPort;

    @Override
    public void createBudget(CreateBudgetCommand command) {
        var importKeyExists = budgetImportKeyExistsInProjectPort.budgetImportKeyExistsForProject(command.getProjectId(), command.getImportKey());
        var nameExists = budgetNameExistsInProjectPort.budgetNameExistsInProject(command.getProjectId(), command.getName());
        if (importKeyExists && nameExists) {
            throw new IllegalArgumentException("importKey and name");
        }
        if (importKeyExists) {
            throw new IllegalArgumentException("importKey");
        }
        if (nameExists) {
            throw new IllegalArgumentException("name");
        }
        createBudgetEntityPort.createBudgetEntity(
                new CreateBudgetEntityPort.CreateBudgetEntityCommand(
                        command.getProjectId(),
                        command.getContractId().orElse(null),
                        command.getName(),
                        command.getDescription(),
                        command.getImportKey(),
                        command.getTotal(),
                        command.getLimit(),
                        command.getTags())
        );
    }
}
