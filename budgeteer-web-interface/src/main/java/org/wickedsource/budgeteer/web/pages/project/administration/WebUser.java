package org.wickedsource.budgeteer.web.pages.project.administration;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class WebUser implements Serializable {
    private long id;
    private String name;
}
