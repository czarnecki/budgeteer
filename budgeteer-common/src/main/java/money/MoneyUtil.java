package money;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.math.RoundingMode;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

public class MoneyUtil {
    private MoneyUtil() {}

    public static Money sum(Collection<Money> data, CurrencyUnit currencyUnit) {
        return sum(data, Function.identity(), currencyUnit);
    }

    public static <T> Money sum(Collection<T> data, Function<T, Money> getter, CurrencyUnit currencyUnit) {
        return data.stream()
                .map(Objects.requireNonNull(getter))
                .reduce(Money.of(currencyUnit, 0), Money::plus);
    }

    public static <T> Money average(Collection<T> data, Function<T, Money> getter, CurrencyUnit currencyUnit) {
        var sum = sum(data, getter, currencyUnit);
        return sum.dividedBy(data.size(), RoundingMode.HALF_DOWN);
    }

    public static Money average(Collection<Money> monies, CurrencyUnit currencyUnit) {
        return average(monies, Function.identity(), currencyUnit);
    }

}
