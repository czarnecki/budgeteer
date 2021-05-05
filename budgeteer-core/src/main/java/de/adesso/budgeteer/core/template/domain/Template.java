package de.adesso.budgeteer.core.template.domain;

import lombok.Value;

@Value
public class Template {
    long id;
    long projectId;
    String name;
    String description;
    Type type;
    boolean isDefault;

    public enum Type {
        BUDGET,
        CONTRACT
    }
}
