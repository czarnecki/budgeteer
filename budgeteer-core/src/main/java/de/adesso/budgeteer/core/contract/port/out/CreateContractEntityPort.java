package de.adesso.budgeteer.core.contract.port.out;

import de.adesso.budgeteer.core.contract.domain.Contract;
import lombok.Value;
import org.joda.money.Money;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

public interface CreateContractEntityPort {
    void createContractEntity(CreateContractEntityCommand command);

    @Value
    class CreateContractEntityCommand {
        long projectId;
        String internalNumber;
        String name;
        Contract.Type type;
        Date startDate;
        Money budget;
        BigDecimal taxRate;
        Map<String, String> attributes;
        String link;
        String fileName;
        byte[] file;
    }
}
