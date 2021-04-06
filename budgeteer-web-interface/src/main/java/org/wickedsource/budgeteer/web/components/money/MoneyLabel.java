package org.wickedsource.budgeteer.web.components.money;


import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.wickedsource.budgeteer.MoneyUtil;
import org.wickedsource.budgeteer.web.components.nullmodel.NullsafeModel;

import java.util.Locale;

public class MoneyLabel extends Label {

    private final IConverter<Money> converter;

    private static final IConverter<Money> PREPEND_CURRENCY_CONVERTER = new MoneyConverter() {
        @Override
        public String convertToString(Money value, Locale locale) {
            String money = super.convertToString(value, locale);
            CurrencyUnit currencyUnit = value.getCurrencyUnit();
            return String.format("%s%s", currencyUnit.getSymbol(locale), money);
        }
    };

    public MoneyLabel(String id, IModel<Money> model) {
        this(id, model, false);
    }

    public MoneyLabel(String id, IModel<Money> model, boolean prependCurrencySymbol) {
        super(id, new NullsafeModel<>(model, MoneyUtil.ZERO));
        IConverter<Money> defaultMoneyConverter = getConverter(Money.class);
        this.converter = !prependCurrencySymbol ? defaultMoneyConverter : PREPEND_CURRENCY_CONVERTER;
    }

    @Override
    protected IConverter<?> createConverter(Class<?> type) {
        if (!Money.class.isAssignableFrom(type)) {
            return null;
        }
        return converter;
    }
}
