package vacancy_tracker.services.telegram.view;

import lombok.experimental.UtilityClass;

@UtilityClass
public class NumbersFormatUtil {

    public static String formatNumber(int number) {
        return String.format("%,d", number)
                .replace(',', ' ');
    }

    public static String formatNumber(Float number) {
        if (number == Math.floor(number)) {
            return String.format("%,d", number.intValue())
                    .replace(',', ' ');
        }
        return String.format("%,.2f", number)
                .replace(',', ' ');
    }
}
