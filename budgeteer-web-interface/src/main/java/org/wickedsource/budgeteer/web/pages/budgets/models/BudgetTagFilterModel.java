package org.wickedsource.budgeteer.web.pages.budgets.models;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Data
public class BudgetTagFilterModel implements Serializable {

    private LinkedHashSet<String> selectedTags;
    private final long projectId;

    public BudgetTagFilterModel(long projectId) {
        this(projectId, new ArrayList<>());
    }

    public BudgetTagFilterModel(long projectId, List<String> selectedTags) {
        this.projectId = projectId;
        this.selectedTags = new LinkedHashSet<>(selectedTags);
    }

    public List<String> getSelectedTags() {
        return new ArrayList<>(selectedTags);
    }

    public boolean isEmpty() {
        return selectedTags.isEmpty();
    }

    public void toggleTag(String tag) {
        if (selectedTags.contains(tag)) {
            selectedTags.remove(tag);
        } else {
            selectedTags.add(tag);
        }
    }

    public boolean isTagSelected(String tag) {
        return selectedTags.contains(tag);
    }

}
