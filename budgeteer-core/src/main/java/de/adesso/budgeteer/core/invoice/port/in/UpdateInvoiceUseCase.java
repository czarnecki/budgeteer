package de.adesso.budgeteer.core.invoice.port.in;

import lombok.Value;
import org.joda.money.Money;

import java.io.File;
import java.util.Date;
import java.util.Map;

public interface UpdateInvoiceUseCase {
    void updateInvoice(UpdateInvoiceCommand command);

    @Value
    class UpdateInvoiceCommand {
        String name;
        String internalNumber;
        Money sum;
        Date startDate;
        Date dueDate;
        Date paidDate;
        File file;
        String link;
        Map<String, String> attributes;
    }
}
