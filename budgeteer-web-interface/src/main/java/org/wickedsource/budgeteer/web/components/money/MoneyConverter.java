package org.wickedsource.budgeteer.web.components.money;

import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.joda.money.Money;
import org.joda.money.format.MoneyFormatter;
import org.joda.money.format.MoneyFormatterBuilder;

import java.util.Locale;

public class MoneyConverter implements IConverter<Money> {

    @Override
    public Money convertToObject(String value, Locale locale) {
        if (value == null) {
            return null;
        }
        MoneyFormatter formatter = new MoneyFormatterBuilder()
                .appendCurrencyCode()
                .appendAmountLocalized()
                .toFormatter(locale);
        try {
            return formatter.parseMoney(value);
        } catch (Exception e) {
            throw new ConversionException(e.getMessage());
        }
    }

    @Override
    public String convertToString(Money value, Locale locale) {
        if (value == null) {
            return null;
        }
        MoneyFormatter formatter = new MoneyFormatterBuilder()
                .appendAmountLocalized()
                .toFormatter(locale);
        return formatter.print(value);
    }
}
