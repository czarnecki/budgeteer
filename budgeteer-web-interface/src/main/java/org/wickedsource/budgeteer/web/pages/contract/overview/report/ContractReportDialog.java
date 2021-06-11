package org.wickedsource.budgeteer.web.pages.contract.overview.report;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wickedsource.budgeteer.service.ReportType;
import org.wickedsource.budgeteer.service.template.TemplateService;
import org.wickedsource.budgeteer.web.BudgeteerSession;
import org.wickedsource.budgeteer.web.Mount;
import org.wickedsource.budgeteer.web.pages.base.dialogpage.DialogPageWithBacklink;
import org.wickedsource.budgeteer.web.pages.contract.overview.report.form.ContractReportForm;

@Mount({"contracts/report"})
public class ContractReportDialog extends DialogPageWithBacklink {

    @SpringBean
	private TemplateService templateService;

	public ContractReportDialog(Class<? extends WebPage> backlinkPage, PageParameters backlinkParameters) {
        super(backlinkPage, backlinkParameters);
        Form<ContractReportMetaInformation> form = new ContractReportForm("form", Model.of(new ContractReportMetaInformation(templateService.getDefault(ReportType.CONTRACT_REPORT, BudgeteerSession.get().getProjectId()))));
        addComponents(form);
	}

    private void addComponents(Form<ContractReportMetaInformation> form) {
        add(createBacklink("cancelButton1"));
        form.add(createBacklink("cancelButton2"));
        add(form);
    }
}
