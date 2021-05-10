package org.wickedsource.budgeteer.web.pages.project.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class WebProject implements Serializable {
    private long id;
    private String name;

    public void setName(String name) {
        System.out.println(name);
        this.name = name;
    }
}
