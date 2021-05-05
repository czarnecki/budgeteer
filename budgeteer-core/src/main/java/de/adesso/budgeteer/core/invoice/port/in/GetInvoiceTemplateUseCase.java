package de.adesso.budgeteer.core.invoice.port.in;

import de.adesso.budgeteer.core.invoice.domain.Invoice;

public interface GetInvoiceTemplateUseCase {
    Invoice getInvoiceTemplate(long contractId);
}
