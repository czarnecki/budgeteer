package de.adesso.budgeteer.core.contract.port.out;

import de.adesso.budgeteer.core.contract.domain.Contract;
import lombok.Value;
import org.joda.money.Money;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

public interface UpdateContractEntityPort {
    void updateContractEntity(UpdateContractEntityCommand command);

    @Value
    class UpdateContractEntityCommand {
        long contractId;
        String name;
        String internalNumber;
        Date startDate;
        Contract.Type type;
        Money budget;
        BigDecimal taxRate;
        Map<String, String> attributes;
        String link;
        String fileName;
        byte[] file;
    }
}
