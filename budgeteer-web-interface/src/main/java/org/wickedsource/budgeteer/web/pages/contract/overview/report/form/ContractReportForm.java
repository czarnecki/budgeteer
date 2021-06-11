package org.wickedsource.budgeteer.web.pages.contract.overview.report.form;

import de.adesso.budgeteer.core.contract.domain.Contract;
import de.adesso.budgeteer.core.contract.port.in.GetContractsInProjectUseCase;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.wickedsource.budgeteer.service.ReportType;
import org.wickedsource.budgeteer.service.template.Template;
import org.wickedsource.budgeteer.service.template.TemplateService;
import org.wickedsource.budgeteer.web.BudgeteerSession;
import org.wickedsource.budgeteer.web.components.customFeedback.CustomFeedbackPanel;
import org.wickedsource.budgeteer.web.components.notificationlist.NotificationListPanel;
import org.wickedsource.budgeteer.web.pages.base.AbstractChoiceRenderer;
import org.wickedsource.budgeteer.web.pages.budgets.overview.report.form.BudgetReportNotificationModel;
import org.wickedsource.budgeteer.web.pages.contract.overview.report.ContractReportMetaInformation;

import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.wicketstuff.lazymodel.LazyModel.from;
import static org.wicketstuff.lazymodel.LazyModel.model;

public class ContractReportForm extends Form<ContractReportMetaInformation> {

    private static final long serialVersionUID = 1L;

    @SpringBean
    private GetContractsInProjectUseCase getContractsInProjectUseCase;

    @SpringBean
    private TemplateService templateService;

    private final List<FormattedDate> formattedMonths;

    public ContractReportForm(String id, IModel<ContractReportMetaInformation> model) {
        super(id, model);
        var months = getMonthsSinceFirstContract();
        var formatter = DateTimeFormatter.ofPattern("yyyy, MMMM");
        formattedMonths = getFormattedMonths(months, formatter);

        getModelObject().setSelectedMonth(formattedMonths.get(formattedMonths.size() - 1));

        DropDownChoice<FormattedDate> choice = new DropDownChoice<>(
                "selectMonth",
                model(from(getModel()).getSelectedMonth()),
                formattedMonths,
                new FormattedDateChoiceRenderer());
        add(choice);

        add(createTemplateDropDown());

        add(new NotificationListPanel("notificationList", new BudgetReportNotificationModel()));
        add(new CustomFeedbackPanel("feedback"));
    }

    private Component createTemplateDropDown() {
        var templateDropDown = new DropDownChoice<>("template", getModel().map(ContractReportMetaInformation::getTemplate),
                () -> {
                    var templates = new ArrayList<Template>();
                    templateService.getTemplatesInProject(BudgeteerSession.get().getProjectId()).stream()
                            .filter(template -> template.getType() == ReportType.CONTRACT_REPORT)
                            .forEach(templates::add);
                    return templates;
                },
                new AbstractChoiceRenderer<>() {
                    @Override
                    public Object getDisplayValue(Template object) {
                        if (object == null) {
                            return "No Template available";
                        }
                        var isDefault = object.isDefault() ? " (default)" : "";
                        return String.format("%s%s", object.getName(), isDefault);
                    }
                });
        templateDropDown.setNullValid(false);
        return templateDropDown;
    }

    private List<FormattedDate> getFormattedMonths(List<LocalDate> months, DateTimeFormatter formatter) {
        return months.stream().map(month -> new FormattedDate(month, formatter)).collect(Collectors.toList());
    }

    @Override
    protected void onSubmit() {
        var filename = "contract-report.xlsx";
        if ((getModelObject()).getTemplate() == null) {
            this.error(getString("feedback.error.no.template"));
            return;
        }
        this.success(getString("feedback.success"));
        IModel<File> fileModel = new ContractReportFileModel(BudgeteerSession.get().getProjectId(), getModel());
        var file = fileModel.getObject();
        IResourceStream resourceStream = new FileResourceStream(file);
        getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream) {
            @Override
            public void respond(IRequestCycle requestCycle) {
                super.respond(requestCycle);
                Files.remove(file);
            }
        }.setFileName(filename).setContentDisposition(ContentDisposition.ATTACHMENT));

    }

    private List<LocalDate> getMonthsSinceFirstContract() {
        var contracts = getContractsInProjectUseCase.getContractsInProject(BudgeteerSession.get().getProjectId());
        if (contracts.isEmpty()) {
            return Collections.emptyList();
        }
        var months = new ArrayList<LocalDate>();
        var firstDate = contracts.stream()
                .map(Contract::getStartDate)
                .min(Date::compareTo)
                .map(date -> Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate())
                .orElseThrow(() -> new IllegalStateException("Should not be reached"));
        var today = LocalDate.now();
        for (var month = firstDate; month.isBefore(today); month = month.plusMonths(1)) {
            months.add(month);
        }
        return months;
    }

    private class FormattedDateChoiceRenderer implements IChoiceRenderer<FormattedDate> {
        @Override
        public Object getDisplayValue(FormattedDate object) {
            return object.getLabel();
        }

        @Override
        public String getIdValue(FormattedDate object, int index) {
            return String.valueOf(formattedMonths.indexOf(object));
        }

        @Override
        public FormattedDate getObject(String id, IModel<? extends List<? extends FormattedDate>> choices) {
            getModelObject().setSelectedMonth(choices.getObject().get(Integer.parseInt(id)));
            return choices.getObject().get(Integer.parseInt(id));
        }
    }
}
