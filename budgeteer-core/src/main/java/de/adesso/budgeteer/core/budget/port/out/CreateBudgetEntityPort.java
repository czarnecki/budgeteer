package de.adesso.budgeteer.core.budget.port.out;

import lombok.Value;
import org.joda.money.Money;

import java.util.List;
import java.util.Optional;

public interface CreateBudgetEntityPort {

    void createBudgetEntity(CreateBudgetEntityCommand command);

    @Value
    class CreateBudgetEntityCommand {
        long projectId;
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
