package org.wickedsource.budgeteer.web.pages.contract.edit.form;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.RangeValidator;
import org.joda.money.Money;
import org.springframework.dao.DataIntegrityViolationException;
import org.wickedsource.budgeteer.persistence.contract.ContractEntity;
import org.wickedsource.budgeteer.service.DateUtil;
import org.wickedsource.budgeteer.service.contract.ContractBaseData;
import org.wickedsource.budgeteer.service.contract.ContractService;
import org.wickedsource.budgeteer.service.contract.DynamicAttributeField;
import org.wickedsource.budgeteer.web.BudgeteerSession;
import org.wickedsource.budgeteer.web.components.customFeedback.CustomFeedbackPanel;
import org.wickedsource.budgeteer.web.components.fileUpload.CustomFileUpload;
import org.wickedsource.budgeteer.web.pages.contract.overview.ContractOverviewPage;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;

import static org.apache.wicket.model.LambdaModel.of;
import static org.wicketstuff.lambda.components.ComponentFactory.ajaxButton;

public class EditContractForm extends GenericPanel<ContractBaseData> {

    @SpringBean
    private ContractService service;

    private boolean createMode;
    private String newAttributeName;

    private final Component table;
    private final Component feedbackPanel;

    public EditContractForm(String id, IModel<ContractBaseData> model) {
        super(id);
        super.setDefaultModel(Objects.requireNonNullElseGet(model, () -> Model.of(service.getEmptyContractModel(BudgeteerSession.get().getProjectId()))));
        this.createMode = model == null;

        var form = new Form<>("form", model);
        feedbackPanel = createFeedbackPanel();
        form.add(feedbackPanel);
        form.add(createContractNameField());
        form.add(createInternalNumberField());
        form.add(createBudgetField());
        form.add(createTaxRateField());
        form.add(createStartDateField());
        form.add(createContractTypeDropDown());
        form.add(createCustomFileUpload());
        table = createAdditionalAttributeInfo();
        form.add(table);
        form.add(createNewAttributeInput());
        form.add(createCancelButton());
        form.add(createSubmitButton());
        add(form);
    }

    private Component createFeedbackPanel() {
        return new CustomFeedbackPanel("feedback")
                .setOutputMarkupId(true);
    }

    private Component createContractNameField() {
        return new TextField<>("contractName", of(getModel(), ContractBaseData::getContractName, ContractBaseData::setContractName))
                .setRequired(true);
    }

    private Component createInternalNumberField() {
        return new TextField<>("internalNumber", of(getModel(), ContractBaseData::getInternalNumber, ContractBaseData::setInternalNumber))
                .setRequired(true);
    }

    private Component createBudgetField() {
        return new TextField<>("budget", of(getModel(), ContractBaseData::getBudget, ContractBaseData::setBudget), Money.class)
                .setRequired(true);
    }

    private Component createTaxRateField() {
        return new TextField<>("taxrate", of(getModel(), ContractBaseData::getTaxRate, ContractBaseData::setTaxRate), BigDecimal.class)
                .setRequired(true)
                .add(RangeValidator.minimum(BigDecimal.ZERO));
    }

    private Component createStartDateField() {
        return new DateTextField("startDate", of(getModel(), baseData -> Objects.requireNonNullElse(baseData.getStartDate(), DateUtil.getBeginOfYear()),
                ContractBaseData::setStartDate))
                .setRequired(true);
    }

    private Component createContractTypeDropDown() {
        return new DropDownChoice<>("type", of(getModel(), ContractBaseData::getType, ContractBaseData::setType),
                Arrays.asList(ContractEntity.ContractType.values()), new EnumChoiceRenderer<>(this));
    }

    private Component createCustomFileUpload() {
        return new CustomFileUpload("fileUpload", of(getModel(), ContractBaseData::getFileModel, ContractBaseData::setFileModel));
    }

    private Component createAdditionalAttributeInfo() {
        var container = new WebMarkupContainer("attributeTable");
        container.setOutputMarkupPlaceholderTag(true);
        container.add(new ListView<>("contractAttributes", getModel().map(ContractBaseData::getContractAttributes)) {
            @Override
            protected void populateItem(ListItem<DynamicAttributeField> item) {
                item.add(new Label("attributeTitle", item.getModel().map(DynamicAttributeField::getName)));
                item.add(new TextField<>("attributeValue", of(item.getModel(), DynamicAttributeField::getValue, DynamicAttributeField::setValue)));
            }
        });
        return container;
    }

    private Component createNewAttributeInput() {
        var container = new WebMarkupContainer("newAttributeInput");
        var newAttributeField = new TextField<>("nameOfNewAttribute", of(() -> newAttributeName, val -> newAttributeName = val)).setOutputMarkupId(true);
        var addButton = ajaxButton("addAttribute", (button, target) -> {
            if (newAttributeName == null) {
                error(getString("feedback.error.nameEmpty"));
                target.add(feedbackPanel);
                return;
            }
            getModel().map(ContractBaseData::getContractAttributes).getObject().add(new DynamicAttributeField(newAttributeName, ""));
            target.add(table, feedbackPanel, newAttributeField);
        }).setOutputMarkupId(true);

        container.add(newAttributeField, addButton);
        return container;
    }

    private Component createCancelButton() {
        return new Link<>("cancel2") {
            @Override
            public void onClick() {
                onCancel();
            }
        };
    }

    private Component createSubmitButton() {
        return new AjaxButton("save", new StringResourceModel(createMode ? "button.save.createmode" : "button.save.editmode")) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    var contractBaseData = EditContractForm.this.getModelObject();
                    contractBaseData.setContractId(service.save(contractBaseData));
                    success(createMode ? getString("feedback.success.creation") : getString("feedback.success"));
                    createMode = false;
                } catch (DataIntegrityViolationException e) {
                    error(getString("feedback.error.dataformat.taxrate"));
                } catch (Exception e) {
                    e.printStackTrace();
                    error(getString("feedback.error"));
                }
                target.add(feedbackPanel);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(feedbackPanel);
            }
        }.setOutputMarkupId(true);
    }

    public void onCancel() {
        setResponsePage(ContractOverviewPage.class);
    }
}
