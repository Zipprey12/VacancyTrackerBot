package vacancy_tracker.services.telegram.command.settings.filter;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.services.telegram.command.InputInterceptingCommand;
import vacancy_tracker.services.telegram.command.handlers.CommandCompletionHandler;
import vacancy_tracker.services.telegram.command.interceptors.InputInterceptor;
import vacancy_tracker.services.telegram.command.publishers.MessagePublisher;
import vacancy_tracker.services.telegram.command.strategy.ExecutionStrategy;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.view.keyboard.KeyboardBuilder;
import vacancy_tracker.services.telegram.view.utils.NumbersFormatUtil;

import java.util.Arrays;
import java.util.LinkedList;

import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.CANCEL_CHANGE;
import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.SET_MIN_SALARY;

public abstract class SetSalaryCommand extends InputInterceptingCommand<Integer> {

    public static final String NEGATIVE_VALUE_MESSAGE = """
            Даже бесплатно работать не стоит, а уж платить за это — тем более.
            Зарплата должна быть *положительным числом*
            """;

    protected SetSalaryCommand(String key, String description,
                               MessagePublisher publisher,
                               CommandCompletionHandler handler,
                               InputInterceptor<Integer> inputInterceptor,
                               SessionsService sessionsService,
                               ExecutionStrategy strategy) {
        super(key, description, publisher, handler, inputInterceptor, sessionsService, strategy);
    }

    protected abstract String getCallbackKey();

    protected InlineKeyboardMarkup initKeyboard(Integer... args) {
        var list = new LinkedList<CallbackItem>();
        if (args != null) {
            Arrays.stream(args).forEach(a -> list.add(createItem(a)));
        }

        list.add(new CallbackItem(getCallbackKey(), "Не указано", 0));
        list.add(new CallbackItem(CANCEL_CHANGE.getKey(), "Оставить текущий"));

        return KeyboardBuilder.buildInlineKeyboard(list, 2);
    }

    protected CallbackItem createItem(int salary) {
        return new CallbackItem(SET_MIN_SALARY.getKey(), NumbersFormatUtil.formatSalary(salary), salary);
    }
}
