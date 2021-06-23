package de.adesso.budgeteer.core.budget.service;

import de.adesso.budgeteer.core.budget.port.in.UpdateBudgetUseCase;
import de.adesso.budgeteer.core.budget.port.out.BudgetHasImportKeyOrUniqueInProjectPort;
import de.adesso.budgeteer.core.budget.port.out.BudgetHasNameOrUniqueInProjectPort;
import de.adesso.budgeteer.core.budget.port.out.UpdateBudgetEntityPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateBudgetService implements UpdateBudgetUseCase {

    private final UpdateBudgetEntityPort updateBudgetEntityPort;
    private final BudgetHasNameOrUniqueInProjectPort budgetHasNameOrUniqueInProjectPort;
    private final BudgetHasImportKeyOrUniqueInProjectPort budgetHasImportKeyOrUniqueInProjectPort;

    @Override
    public void updateBudget(UpdateBudgetCommand command) {
        var hasImportKeyOrIsUnique = budgetHasImportKeyOrUniqueInProjectPort.budgetHasImportKeyOrUniqueInProject(command.getBudgetId(), command.getImportKey());
        var hasNameOrIsUnique = budgetHasNameOrUniqueInProjectPort.budgetHasNameOrUniqueInProject(command.getBudgetId(), command.getName());

        if (!(hasImportKeyOrIsUnique || hasNameOrIsUnique)) {
            throw new IllegalArgumentException("importKey and name");
        }
        if (!hasImportKeyOrIsUnique) {
            throw new IllegalArgumentException("importKey");
        }
        if (!hasNameOrIsUnique) {
            throw new IllegalArgumentException("name");
        }
        updateBudgetEntityPort.updateBudgetEntity(new UpdateBudgetEntityPort.UpdateBudgetEntityCommand(
                command.getBudgetId(),
                command.getContractId().orElse(null),
                command.getName(),
                command.getDescription(),
                command.getImportKey(),
                command.getTotal(),
                command.getLimit(),
                command.getTags()
        ));
    }
}
