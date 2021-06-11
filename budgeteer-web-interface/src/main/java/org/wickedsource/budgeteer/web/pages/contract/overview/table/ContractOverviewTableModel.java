package org.wickedsource.budgeteer.web.pages.contract.overview.table;

import lombok.Data;
import org.wickedsource.budgeteer.web.pages.contract.model.ContractModel;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Data
public class ContractOverviewTableModel implements Serializable{
    private List<ContractModel> contracts = new LinkedList<>();
    private List<String> footer = new LinkedList<>();
    private boolean taxRateEnabled;
}
