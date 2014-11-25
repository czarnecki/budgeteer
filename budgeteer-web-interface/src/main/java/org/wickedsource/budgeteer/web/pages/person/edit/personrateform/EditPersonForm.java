package org.wickedsource.budgeteer.web.pages.person.edit.personrateform;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wickedsource.budgeteer.service.person.PersonRate;
import org.wickedsource.budgeteer.service.person.PersonService;
import org.wickedsource.budgeteer.service.person.PersonWithRates;
import org.wickedsource.budgeteer.web.components.money.BudgetUnitMoneyModel;
import org.wickedsource.budgeteer.web.components.money.MoneyLabel;
import org.wickedsource.budgeteer.web.pages.person.edit.EditPersonPage;
import org.wickedsource.budgeteer.web.pages.person.edit.IEditPersonPageStrategy;

import static org.wicketstuff.lazymodel.LazyModel.from;
import static org.wicketstuff.lazymodel.LazyModel.model;

public class EditPersonForm extends Form<PersonWithRates> {

    @SpringBean
    private PersonService peopleService;

    private IEditPersonPageStrategy strategy;

    /**
     * Use this constructor for a form that creates a new user.
     */
    public EditPersonForm(String id, IEditPersonPageStrategy strategy) {
        super(id, new PersonWithRatesModel(new PersonWithRates()));
        this.strategy = strategy;
        Injector.get().inject(this);
        addComponents();
    }

    /**
     * Use this constructor for a form that edits and existing user.
     */
    public EditPersonForm(String id, PersonWithRates person, IEditPersonPageStrategy strategy) {
        super(id, new PersonWithRatesModel(person));
        this.strategy = strategy;
        addComponents();
    }

    private void addComponents() {
        setOutputMarkupId(true);
        add(new FeedbackPanel("feedback"));
        add(new RequiredTextField<String>("name", model(from(getModel()).getName())));
        add(new RequiredTextField<String>("importKey", model(from(getModel()).getImportKey())));
        add(createRatesList("ratesList"));

        add(new PersonRateForm("addRateForm") {
            @Override
            protected void rateAdded(PersonRate rate) {
                EditPersonForm.this.getModelObject().getRates().add(rate);
            }
        });

        Button submitButton = new Button("submitButton");
        submitButton.add(strategy.createSubmitButtonLabel("submitButtonTitle"));

        add(submitButton);
    }

    private ListView<PersonRate> createRatesList(String id) {
        return new ListView<PersonRate>(id, model(from(getModel()).getRates())) {
            @Override
            protected void populateItem(final ListItem<PersonRate> item) {
                item.add(new MoneyLabel("rate", new BudgetUnitMoneyModel(model(from(item.getModel()).getRate()))));
                item.add(new Label("budget", model(from(item.getModel()).getBudget().getName())));
                item.add(new Label("startDate", model(from(item.getModel()).getDateRange().getStartDate())));
                item.add(new Label("endDate", model(from(item.getModel()).getDateRange().getEndDate())));
                Button deleteButton = new Button("deleteButton") {
                    @Override
                    public void onSubmit() {
                        EditPersonForm.this.getModelObject().getRates().remove(item.getModelObject());
                    }
                };
                deleteButton.setDefaultFormProcessing(false);
                item.add(deleteButton);
            }

            @Override
            protected ListItem<PersonRate> newItem(int index, IModel<PersonRate> itemModel) {
                // wrap into Model that can be used with LazyModel
                return super.newItem(index, new PersonRateModel(itemModel));
            }
        };
    }

    @Override
    protected void onSubmit() {
        peopleService.savePersonWithRates(getModelObject());
        ((EditPersonPage) getPage()).goBack();
    }


}
