package vacancy_tracker.services.telegram.view.formatters.filter;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.services.telegram.view.keyboard.KeyboardBuilder;
import vacancy_tracker.services.telegram.view.utils.NumbersFormatUtil;

import java.util.Arrays;
import java.util.LinkedList;

import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.CANCEL_CHANGE;

@UtilityClass
public class SalaryFormatter {

    public static final String NEGATIVE_VALUE_MESSAGE = """
            Даже бесплатно работать не стоит, а уж платить за это — тем более.
            Зарплата должна быть *положительным числом*
            """;

    public static InlineKeyboardMarkup createKeyboard(String callback, Integer... args) {
        var list = new LinkedList<CallbackItem>();
        if (args != null) {
            Arrays.stream(args).forEach(a -> list.add(createItem(callback, a)));
        }

        list.add(new CallbackItem(callback, "Не указывать", 0));
        list.add(new CallbackItem(CANCEL_CHANGE.getKey(), "Оставить текущий"));

        return KeyboardBuilder.buildInlineKeyboard(list, 2);
    }

    public static CallbackItem createItem(String callback, int salary) {
        return new CallbackItem(callback, NumbersFormatUtil.formatSalary(salary), salary);
    }
}
