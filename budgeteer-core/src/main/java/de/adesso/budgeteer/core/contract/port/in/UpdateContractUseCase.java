package de.adesso.budgeteer.core.contract.port.in;

import de.adesso.budgeteer.core.contract.domain.Contract;
import lombok.Value;
import org.joda.money.Money;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

public interface UpdateContractUseCase {
    void updateContract(UpdateContractCommand command);

    @Value
    class UpdateContractCommand {
        long projectId;
        String name;
        String internalNumber;
        Date startDate;
        Contract.Type type;
        Money budget;
        BigDecimal taxRate;
        String link;
        File file;
        Map<String, String> attributes;
    }
}
