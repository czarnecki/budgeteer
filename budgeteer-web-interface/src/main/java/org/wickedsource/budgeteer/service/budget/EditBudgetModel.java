package org.wickedsource.budgeteer.service.budget;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.money.Money;
import org.wickedsource.budgeteer.web.pages.contract.model.ContractModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class EditBudgetModel implements Serializable {

    private long id;
    private long projectId;
    private String title;
    private String description;
    private String note;
    private Money total;
    private Money limit;
    private String importKey;
    private List<String> tags;
    private ContractModel contract;

    public EditBudgetModel(long projectId) {
        this.projectId = projectId;
        this.tags = new ArrayList<>();
    }

    public void setTags(List<String> tags) {
        if (tags != null) {
            this.tags = new ArrayList<>(tags);
        }
    }
}
