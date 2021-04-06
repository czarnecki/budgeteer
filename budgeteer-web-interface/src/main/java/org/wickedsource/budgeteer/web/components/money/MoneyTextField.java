package org.wickedsource.budgeteer.web.components.money;


import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.convert.IConverter;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

public class MoneyTextField extends FormComponentPanel<Money> {

    private TextField<String> moneyInput;
    private DropDownChoice<CurrencyUnit> currencyUnitInput;

    public MoneyTextField(String id) {
        this(id, Model.of());
    }

    public MoneyTextField(String id, IModel<Money> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setType(Money.class);

        moneyInput = new TextField<>("money", Model.of(getConverter(getType()).convertToString(getModelObject(), getLocale())));
        add(moneyInput);

        currencyUnitInput = new DropDownChoice<>("currencyUnit", Model.of(CurrencyUnit.of(getLocale())), CurrencyUnit.registeredCurrencies());
        add(currencyUnitInput);
    }

    @Override
    public void convertInput() {
        String inputMoney = moneyInput.getConvertedInput();
        CurrencyUnit currencyUnit = currencyUnitInput.getConvertedInput();

        if (inputMoney == null) {
            return;
        }

        IConverter<Money> moneyConverter = getConverter(getType());
        Money money = moneyConverter.convertToObject(String.format("%s%s", currencyUnit, inputMoney), getLocale());

        setModelObject(money);
        setConvertedInput(money);
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();

        Money value = getModelObject();
        if (value == null) {
            return;
        }

        IConverter<Money> moneyConverter = getConverter(getType());
        String input = moneyConverter.convertToString(value, getLocale());

        CurrencyUnit currencyUnit = value.getCurrencyUnit();

        moneyInput.setModelObject(input);
        currencyUnitInput.setModelObject(currencyUnit);
    }
}
