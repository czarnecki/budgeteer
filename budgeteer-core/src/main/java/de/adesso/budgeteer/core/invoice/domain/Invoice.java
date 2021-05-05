package de.adesso.budgeteer.core.invoice.domain;

import de.adesso.budgeteer.core.common.Attachment;
import lombok.Value;
import org.joda.money.Money;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Value
public class Invoice {
    long id;
    long contractId;
    String name;
    String contractName;
    Money sum;
    Money sumGross; /* TODO: decide if it should be a method */
    Money taxAmount; /* TODO: decide if it should be a method */
    BigDecimal taxRate;
    String internalNumber;
    Date startDate;
    Date dueDate;
    Date paidDate;
    Attachment attachment;
    Map<String, String> attributes;
}
