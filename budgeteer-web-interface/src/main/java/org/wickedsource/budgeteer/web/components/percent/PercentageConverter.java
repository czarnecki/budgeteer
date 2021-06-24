package org.wickedsource.budgeteer.web.components.percent;

import org.apache.wicket.util.convert.IConverter;

import java.util.Locale;

public class PercentageConverter implements IConverter<Double> {

    @Override
    public Double convertToObject(String value, Locale locale) {
        throw new UnsupportedOperationException("This converter is a read-only converter and only supports converting one-way");
    }

    @Override
    public String convertToString(Double value, Locale locale) {
        return String.format("%.2f%%", value);
    }

}
