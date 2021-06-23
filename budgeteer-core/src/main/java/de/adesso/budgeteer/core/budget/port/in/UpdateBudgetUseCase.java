package de.adesso.budgeteer.core.budget.port.in;

import lombok.Value;
import org.joda.money.Money;

import java.util.List;
import java.util.Optional;

public interface UpdateBudgetUseCase {
    void updateBudget(UpdateBudgetCommand command);

    @Value
    class UpdateBudgetCommand {
        long budgetId;
        Long contractId;
        String name;
        String description;
        String importKey;
        Money total;
        Money limit;
        List<String> tags;

        public Optional<Long> getContractId() {
            return Optional.ofNullable(contractId);
        }
    }
}
