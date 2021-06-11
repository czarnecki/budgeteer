package org.wickedsource.budgeteer.web.pages.contract.overview.report.form;

import de.adesso.budgeteer.core.contract.service.ExportContractService;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wickedsource.budgeteer.web.pages.contract.overview.report.ContractReportMetaInformation;

import java.io.File;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ContractReportFileModel extends LoadableDetachableModel<File> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    
    @SpringBean
    private ExportContractService exportContractService;

    private long projectId;
    
    private IModel<ContractReportMetaInformation> reportModel;

    public ContractReportFileModel(long projectId, IModel<ContractReportMetaInformation> reportModel) {
        Injector.get().inject(this);
        this.projectId = projectId;
        this.reportModel = reportModel;
    }

    @Override
    protected File load() {
    	var now = LocalDate.now();
    	var adjustedDate = reportModel.getObject().getSelectedMonth().getDate().plus(1,ChronoUnit.MONTHS).minus(1,ChronoUnit.DAYS);
    	LocalDate endDate = now.isBefore(adjustedDate) ? now : adjustedDate;
        return exportContractService.exportContract(projectId, reportModel.getObject().getTemplate().getId(), endDate);
    }

}
