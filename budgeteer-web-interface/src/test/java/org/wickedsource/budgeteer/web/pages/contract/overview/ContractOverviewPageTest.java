package org.wickedsource.budgeteer.web.pages.contract.overview;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.wickedsource.budgeteer.web.AbstractWebTestTemplate;
import org.wickedsource.budgeteer.web.pages.contract.overview.table.ContractOverviewTableModel;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class ContractOverviewPageTest extends AbstractWebTestTemplate {

    @Autowired
    private ContractService contractServiceMock;

    @Test
    void testRender() {
        ContractOverviewPage page = new ContractOverviewPage();
        getTester().startPage(page);
        getTester().assertRenderedPage(ContractOverviewPage.class);
    }

    @Override
    protected void setupTest() {
        when(contractServiceMock.getContractOverviewByProject(anyLong())).thenReturn(new ContractOverviewTableModel());
    }
}
