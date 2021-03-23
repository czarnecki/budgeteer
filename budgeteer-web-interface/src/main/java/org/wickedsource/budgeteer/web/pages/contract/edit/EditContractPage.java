package org.wickedsource.budgeteer.web.pages.contract.edit;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;
import org.wickedsource.budgeteer.service.contract.ContractService;
import org.wickedsource.budgeteer.web.BudgeteerSession;
import org.wickedsource.budgeteer.web.Mount;
import org.wickedsource.budgeteer.web.pages.base.dialogpage.DialogPage;
import org.wickedsource.budgeteer.web.pages.contract.edit.form.EditContractForm;
import org.wickedsource.budgeteer.web.pages.contract.overview.ContractOverviewPage;

@Mount({"contracts/edit/#{id}"})
public class EditContractPage extends DialogPage {

    @SpringBean
    private ContractService service;

    public EditContractPage(PageParameters parameters) {
        super(parameters);
        var title = getContractId() == 0 ? "Create Contract" : "Edit Contract";
        var model = Model.of(getContractId() == 0
                ? service.getEmptyContractModel(BudgeteerSession.get().getProjectId())
                : service.getContractById(getContractId()));
        add(new Label("pageTitle", Model.of(title)));
        add(new EditContractForm("form", model) {
            @Override
            public void onCancel() {
                onBackLinkClicked();
            }
        });
        add(new Link<>("cancelButton1") {
            @Override
            public void onClick() {
                onBackLinkClicked();
            }
        });
    }

    public EditContractPage() {
        this(null);
    }

    private long getContractId() {
        StringValue value = getPageParameters().get("id");
        if (value == null || value.isEmpty() || value.isNull()) {
            return 0L;
        } else {
            return value.toLong();
        }
    }

    public static PageParameters createParameters(long contractId) {
        PageParameters parameters = new PageParameters();
        parameters.add("id", contractId);
        return parameters;
    }

    public void onBackLinkClicked() {
        setResponsePage(ContractOverviewPage.class, new PageParameters());
    }
}
