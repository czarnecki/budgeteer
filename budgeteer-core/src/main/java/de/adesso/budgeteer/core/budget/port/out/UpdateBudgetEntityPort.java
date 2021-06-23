package de.adesso.budgeteer.core.budget.port.out;

import lombok.Value;
import org.joda.money.Money;

import java.util.List;
import java.util.Optional;

public interface UpdateBudgetEntityPort {
    void updateBudgetEntity(UpdateBudgetEntityCommand command);

    @Value
    class UpdateBudgetEntityCommand {
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
