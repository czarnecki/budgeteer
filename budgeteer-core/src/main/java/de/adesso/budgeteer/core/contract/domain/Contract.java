package de.adesso.budgeteer.core.contract.domain;

import lombok.Value;
import org.joda.money.Money;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Value
public class Contract {
    long id;
    long projectId;
    String internalNumber;
    String name;
    Type type;
    Date startDate;
    Money budget;
    Money budgetSpent;
    Money budgetLeft;
    BigDecimal taxRate;
    Map<String, String> attributes;

    public enum Type {
        TIME_AND_MATERIAL,
        FIXED_PRICE
    }
}
