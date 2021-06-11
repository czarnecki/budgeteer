package de.adesso.budgeteer.core.template.domain;

import lombok.Value;
import org.apache.poi.ss.usermodel.Workbook;

@Value
public class Template {
    long id;
    long projectId;
    String name;
    String description;
    Type type;
    boolean isDefault;
    Workbook workbook;

    public enum Type {
        BUDGET,
        CONTRACT
    }
}
