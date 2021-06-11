package org.wickedsource.budgeteer.web.pages.contract.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.money.Money;
import org.wickedsource.budgeteer.web.components.fileUpload.FileUploadModel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractModel implements Serializable {
    private long id;
    private long projectId;
    private String internalNumber;
    private String name;
    private Type type;
    private Date startDate;
    private Money budget;
    private Money budgetSpent;
    private Money budgetLeft;
    private BigDecimal taxRate;
    private List<Attribute> attributes;
    private FileUploadModel fileUploadModel;

    public ContractModel(long projectId, List<String> attributeKeys) {
        this.projectId = projectId;
        this.attributes = attributeKeys.stream().map(Attribute::new).collect(Collectors.toList());
        this.fileUploadModel = new FileUploadModel();
    }

    public Money getBudgetGross() {
        return applyTax(budget);
    }

    public Money getBudgetSpentGross() {
        return applyTax(budgetSpent);
    }

    public Money getBudgetLeftGross() {
        return applyTax(budgetLeft);
    }

    public BigDecimal getTaxCoefficient() {
        return BigDecimal.ONE.add(taxRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_DOWN));
    }

    public Map<String, String> getAttributesAsMap() {
        return attributes.stream()
                .collect(Collectors.toMap(Attribute::getKey,
                        attribute -> Objects.requireNonNullElse(attribute.getValue(), ""),
                        (a, b) -> a,
                        LinkedHashMap::new)); // LinkedHashMap to preserve insertion order
    }

    private Money applyTax(Money money) {
        return money.multipliedBy(getTaxCoefficient(), RoundingMode.HALF_DOWN);
    }

    @Data
    public static class Attribute implements Serializable {
        private String key;
        private String value;

        public Attribute(String key) {
            this.key = key;
        }

        public Attribute(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    public enum Type {
        TIME_AND_MATERIAL,
        FIXED_PRICE
    }
}
