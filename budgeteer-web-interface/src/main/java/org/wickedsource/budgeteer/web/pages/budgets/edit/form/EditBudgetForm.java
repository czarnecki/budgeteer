package org.wickedsource.budgeteer.web.pages.budgets.edit.form;

import de.adesso.budgeteer.core.contract.port.in.GetContractsInProjectUseCase;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LambdaModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wickedsource.budgeteer.service.budget.BudgetService;
import org.wickedsource.budgeteer.service.budget.EditBudgetModel;
import org.wickedsource.budgeteer.web.BudgeteerSession;
import org.wickedsource.budgeteer.web.ClassAwareWrappingModel;
import org.wickedsource.budgeteer.web.components.customFeedback.CustomFeedbackPanel;
import org.wickedsource.budgeteer.web.components.money.MoneyTextField;
import org.wickedsource.budgeteer.web.components.notificationlist.NotificationListPanel;
import org.wickedsource.budgeteer.web.pages.base.AbstractChoiceRenderer;
import org.wickedsource.budgeteer.web.pages.budgets.BudgetTagsModel;
import org.wickedsource.budgeteer.web.pages.budgets.edit.EditBudgetPage;
import org.wickedsource.budgeteer.web.pages.budgets.edit.tagsfield.TagsTextField;
import org.wickedsource.budgeteer.web.pages.budgets.exception.InvalidBudgetImportKeyAndNameException;
import org.wickedsource.budgeteer.web.pages.budgets.exception.InvalidBudgetImportKeyException;
import org.wickedsource.budgeteer.web.pages.budgets.exception.InvalidBudgetNameException;
import org.wickedsource.budgeteer.web.pages.budgets.overview.BudgetsOverviewPage;
import org.wickedsource.budgeteer.web.pages.contract.model.ContractModel;
import org.wickedsource.budgeteer.web.pages.contract.model.ContractModelMapper;

import java.util.List;

import static org.wicketstuff.lazymodel.LazyModel.from;
import static org.wicketstuff.lazymodel.LazyModel.model;

public class EditBudgetForm extends Form<EditBudgetModel> {

    @SpringBean
    private BudgetService service;

    @SpringBean
    private GetContractsInProjectUseCase getContractsInProjectUseCase;

    @SpringBean
    private ContractModelMapper contractModelMapper;

    private boolean isEditing;

    public EditBudgetForm(String id) {
        super(id, new ClassAwareWrappingModel<>(Model.of(new EditBudgetModel(BudgeteerSession.get().getProjectId())), EditBudgetModel.class));
        addComponents();
        this.isEditing = false;
    }

    public EditBudgetForm(String id, IModel<EditBudgetModel> model, boolean isEditingNewBudget) {
        super(id, model);
        this.isEditing = true;
        Injector.get().inject(this);
        addComponents();
        if (isEditingNewBudget) {
            this.success("Budget successfully created.");
        }
    }

    private void addComponents() {
        TagsTextField tagsField = new TagsTextField("tagsInput", model(from(getModel()).getTags()));
        tagsField.setOutputMarkupId(true);
        add(tagsField);
        tagsField.add(new AjaxEventBehavior("change") {
            @SuppressWarnings("unchecked")
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                if (getModelObject().getTags().size() > 0) {
                    BudgeteerSession.get().getBudgetFilter().getSelectedTags().remove(getModelObject().getTags().remove(0));
                }
            }
        });
        add(new CustomFeedbackPanel("feedback"));
        add(new RequiredTextField<>("name", model(from(getModel()).getTitle())));
        add(new TextField<>("description", model(from(getModel()).getDescription())));
        add(new RequiredTextField<>("importKey", model(from(getModel()).getImportKey())));
        MoneyTextField totalField = new MoneyTextField("total", model(from(getModel()).getTotal()));
        totalField.setRequired(true);
        add(totalField);
        MoneyTextField limitField = new MoneyTextField("limit", model(from(getModel()).getLimit()));
        add(limitField);
        var contractDropDown = new DropDownChoice<>("contract", LambdaModel.of(getModel(), EditBudgetModel::getContract, EditBudgetModel::setContract),
                contractModelMapper.mapToModel(getContractsInProjectUseCase.getContractsInProject(BudgeteerSession.get().getProjectId())),
                new AbstractChoiceRenderer<>() {
                    @Override
                    public Object getDisplayValue(ContractModel contractModel) {
                        return contractModel == null ? getString("no.contract") : contractModel.getName();
                    }
                });
        contractDropDown.setNullValid(true);
        add(contractDropDown);
        add(createTagsList(new BudgetTagsModel(BudgeteerSession.get().getProjectId()), tagsField));
        add(new NotificationListPanel("notificationList", new BudgetNotificationsModel(getModel().getObject().getId())));

        //Label for the submit button
        Label submitButtonLabel;
        if (isEditing) {
            submitButtonLabel = new Label("submitButtonLabel", new ResourceModel("button.save.editmode"));
        } else {
            submitButtonLabel = new Label("submitButtonLabel", new ResourceModel("button.save.createmode"));
        }
        add(submitButtonLabel);
    }

    private ListView<String> createTagsList(IModel<List<String>> model, TagsTextField tagsField) {
        return new ListView<>("tagsList", model) {
            @Override
            protected void populateItem(final ListItem<String> item) {
                var label = new Label("tag", model(from(item.getModel())));
                label.setRenderBodyOnly(true);
                item.add(label);
                item.add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        tagsField.getModelObject().add(item.getModelObject());
                        target.appendJavaScript(String.format("$('#%s').tagsinput('add', '%s');", tagsField.getMarkupId(), item.getModelObject()));
                    }
                });
            }

            @Override
            protected ListItem<String> newItem(int index, IModel<String> itemModel) {
                return super.newItem(index, new ClassAwareWrappingModel<>(itemModel, String.class));
            }
        };
    }

    @Override
    protected void onSubmit() {
        try {
            if (!isEditing) {
                //This prevents the user from creating a completely new budget when trying to
                //edit a newly created budget from the same form
                isEditing = true;
                long newID = service.saveBudget(getModelObject());
                setResponsePage(new EditBudgetPage(EditBudgetPage.createParameters(
                        newID), BudgetsOverviewPage.class, new PageParameters(), true));
            } else {
                service.saveBudget(getModelObject());
                this.success(getString("feedback.success"));
            }
        } catch (InvalidBudgetImportKeyAndNameException e) {
            this.error(getString("feedback.error.duplicateImportKey"));
            this.error(getString("feedback.error.duplicateName"));
        } catch (InvalidBudgetImportKeyException e) {
            this.error(getString("feedback.error.duplicateImportKey"));
        } catch (InvalidBudgetNameException e) {
            this.error(getString("feedback.error.duplicateName"));
        }
    }
}
