package org.wickedsource.budgeteer.web.pages.contract.edit.form;

import de.adesso.budgeteer.core.contract.domain.Contract;
import de.adesso.budgeteer.core.contract.port.in.CreateContractUseCase;
import de.adesso.budgeteer.core.contract.port.in.UpdateContractUseCase;
import de.adesso.budgeteer.core.project.port.in.GetProjectAttributesUseCase;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LambdaModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.RangeValidator;
import org.springframework.dao.DataIntegrityViolationException;
import org.wickedsource.budgeteer.service.DateUtil;
import org.wickedsource.budgeteer.web.BudgeteerSession;
import org.wickedsource.budgeteer.web.components.customFeedback.CustomFeedbackPanel;
import org.wickedsource.budgeteer.web.components.daterange.DateInputField;
import org.wickedsource.budgeteer.web.components.fileUpload.CustomFileUpload;
import org.wickedsource.budgeteer.web.components.money.MoneyTextField;
import org.wickedsource.budgeteer.web.pages.contract.model.ContractModel;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;

public class EditContractForm extends Form<ContractModel> {


    @SpringBean
    private UpdateContractUseCase updateContractUseCase;

    @SpringBean
    private CreateContractUseCase createContractUseCase;

    @SpringBean
    private GetProjectAttributesUseCase getProjectAttributesUseCase;

    private WebMarkupContainer table;

    private TextField<String> newAttributeField;
    private CustomFeedbackPanel feedbackPanel;

    private boolean editMode;

    private static final String EDIT_PROPERTY = "button.save.editMode";
    private static final String CREATE_PROPERTY = "button.save.createMode";

    public EditContractForm(String id) {
        this(id, null, false);
    }

    public EditContractForm(String id, IModel<ContractModel> model) {
        this(id, model, true);
    }

    private EditContractForm(String id, IModel<ContractModel> model, boolean editMode) {
        super(id);
        this.editMode = editMode;
        long projectId = BudgeteerSession.get().getProjectId();
        super.setDefaultModel(Objects.requireNonNullElseGet(model, () -> Model.of(new ContractModel(projectId, getProjectAttributesUseCase.getProjectAttributes(projectId)))));
        addComponents();
    }

    private void addComponents() {
        feedbackPanel = new CustomFeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);

        var nameTextField = new TextField<>("contractName", LambdaModel.of(getModel(), ContractModel::getName, ContractModel::setName))
                .setRequired(true);
        add(nameTextField);

        var internalNumberTextField = new TextField<>("internalNumber", LambdaModel.of(getModel(), ContractModel::getInternalNumber, ContractModel::setInternalNumber))
                .setRequired(true);
        add(internalNumberTextField);

        var budgetTextField = new MoneyTextField("budget", LambdaModel.of(getModel(), ContractModel::getBudget, ContractModel::setBudget))
                .setRequired(true);
        add(budgetTextField);

        var taxRateTextField = new TextField<>("taxrate", LambdaModel.of(getModel(), ContractModel::getTaxRate, ContractModel::setTaxRate), BigDecimal.class);
        taxRateTextField.setRequired(true);
        taxRateTextField.add(RangeValidator.minimum(BigDecimal.ZERO));
        add(taxRateTextField);

        var startDateInputField = new DateInputField("startDate",
                LambdaModel.of(getModel(),
                        contractModel -> Objects.requireNonNullElseGet(contractModel.getStartDate(), DateUtil::getBeginOfYear),
                        ContractModel::setStartDate), DateInputField.DROP_LOCATION.UP);
        startDateInputField.setRequired(true);
        add(startDateInputField);

        add(new DropDownChoice<>("type",
                LambdaModel.of(getModel(), ContractModel::getType, ContractModel::setType), Arrays.asList(ContractModel.Type.values()),
                new EnumChoiceRenderer<>(this)));

        var fileUpload = new CustomFileUpload("fileUpload", LambdaModel.of(getModel(), ContractModel::getFileUploadModel, ContractModel::setFileUploadModel));
        add(fileUpload);

        table = new WebMarkupContainer("attributeTable");
        table.setOutputMarkupId(true);
        table.setOutputMarkupPlaceholderTag(true);
        table.add(new ListView<>("contractAttributes", getModel().map(ContractModel::getAttributes)) {
            @Override
            protected void populateItem(ListItem<ContractModel.Attribute> item) {
                item.add(new Label("attributeTitle", item.getModel().map(ContractModel.Attribute::getKey)));
                item.add(new TextField<>("attributeValue", LambdaModel.of(item.getModel(), ContractModel.Attribute::getValue, ContractModel.Attribute::setValue)));
            }
        });
        add(table);
        newAttributeField = new TextField<>("nameOfNewAttribute", Model.of(""));
        newAttributeField.setOutputMarkupId(true);
        add(newAttributeField);
        Button addAttribute = new AjaxButton("addAttribute") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                if (newAttributeField.getModelObject() != null) {
                    var contractModel = EditContractForm.this.getModelObject();
                    contractModel.getAttributes().add(new ContractModel.Attribute(newAttributeField.getModelObject()));
                    target.add(table, newAttributeField, feedbackPanel);
                } else {
                    this.error(getString("feedback.error.nameEmpty"));
                    target.add(feedbackPanel);
                }
            }
        };
        addAttribute.setOutputMarkupId(true);
        add(addAttribute);
        add(new AjaxButton("save", new StringResourceModel(editMode ? EDIT_PROPERTY : CREATE_PROPERTY)) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    var contractModel = EditContractForm.this.getModelObject();
                    if (!editMode) {
                        editMode = true;
                        createContractUseCase.createContract(new CreateContractUseCase.CreateContractCommand(
                                contractModel.getProjectId(),
                                contractModel.getName(),
                                contractModel.getInternalNumber(),
                                contractModel.getStartDate(),
                                contractModel.getType() == ContractModel.Type.FIXED_PRICE ? Contract.Type.FIXED_PRICE : Contract.Type.TIME_AND_MATERIAL,
                                contractModel.getBudget(),
                                contractModel.getTaxRate(),
                                contractModel.getAttributesAsMap(),
                                contractModel.getFileUploadModel().getLink(),
                                contractModel.getFileUploadModel().getFileName(),
                                contractModel.getFileUploadModel().getFile()
                        ));
                        setDefaultModel(new StringResourceModel(EDIT_PROPERTY));
                        target.add(this);
                        this.success(getString("feedback.success.creation"));
                    } else {
                        updateContractUseCase.updateContract(new UpdateContractUseCase.UpdateContractCommand(
                                contractModel.getId(),
                                contractModel.getName(),
                                contractModel.getInternalNumber(),
                                contractModel.getStartDate(),
                                contractModel.getType() == ContractModel.Type.FIXED_PRICE ? Contract.Type.FIXED_PRICE : Contract.Type.TIME_AND_MATERIAL,
                                contractModel.getBudget(),
                                contractModel.getTaxRate(),
                                contractModel.getAttributesAsMap(),
                                contractModel.getFileUploadModel().getLink(),
                                contractModel.getFileUploadModel().getFileName(),
                                contractModel.getFileUploadModel().getFile()
                        ));
                        this.success(getString("feedback.success"));
                    }
                } catch (DataIntegrityViolationException e) {
                    this.error(getString("feedback.error.dataformat.taxrate"));
                } catch (Exception e) {
                    e.printStackTrace();
                    this.error(getString("feedback.error"));
                }
                target.add(feedbackPanel);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(feedbackPanel);
            }
        }.setOutputMarkupId(true));
    }
}
