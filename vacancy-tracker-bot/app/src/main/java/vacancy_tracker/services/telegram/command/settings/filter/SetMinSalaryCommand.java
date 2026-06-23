package vacancy_tracker.services.telegram.command.settings.filter;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.telegram.command.CommandArgs;
import vacancy_tracker.model.telegram.command.CommandCategory;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.InputInterceptingCommand;
import vacancy_tracker.services.telegram.command.handlers.FiltersChangingCompletionHandler;
import vacancy_tracker.services.telegram.command.interceptors.IntegerInterceptor;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.command.strategy.SequentialAsyncExecutionStrategy;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SearchFiltersService;
import vacancy_tracker.services.telegram.view.formatters.filter.SalaryFormatter;
import vacancy_tracker.services.telegram.view.utils.NumbersFormatUtil;

import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.SET_MIN_SALARY;
import static vacancy_tracker.services.telegram.view.formatters.filter.SalaryFormatter.NEGATIVE_VALUE_MESSAGE;

@Component
public class SetMinSalaryCommand extends InputInterceptingCommand<Integer> {

    public static final String KEY = "/min_salary";
    public static final String DESCRIPTION = "Минимальная зарплата";

    private final InlineKeyboardMarkup keyboardMarkup = initKeyboard();

    private final SearchFiltersService settingsService;

    protected SetMinSalaryCommand(SendingAndUpdatingMessagePublisher publisher,
                                  SessionsService sessionsService,
                                  SearchFiltersService settingsService,
                                  FiltersChangingCompletionHandler completionHandler,
                                  SequentialAsyncExecutionStrategy strategy) {
        super(new CommandArgs(KEY, DESCRIPTION, completionHandler, CommandCategory.FILTER), publisher,
                new IntegerInterceptor(), sessionsService, strategy);

        this.settingsService = settingsService;
    }

    private static String createText(Integer minSalary) {
        String secondPart = "Чтобы *изменить:* выберите вариант из списка или отправьте число. " +
                "Например:  `50 000`";

        return createHeader(minSalary) + secondPart;
    }

    private static String createHeader(Integer minSalary) {
        String value;
        if (minSalary == null || minSalary <= 0) {
            value = "не указано";
        } else {
            value = NumbersFormatUtil.formatSalary(minSalary);
        }

        return "Текущее значение минимальной зарплаты: *" + value + "*\n";
    }

    @Override
    protected void executeWithParameters(MessageData messageData, Integer parameter) {
        var chatId = messageData.getChatId();
        var filters = settingsService.get(chatId);
        var maxSalary = filters.getMaxSalary();

        if (parameter < 0) {
            handleInvalidValue(messageData, NEGATIVE_VALUE_MESSAGE);
            return;
        }
        if (maxSalary != null && parameter > maxSalary) {
            handleInvalidValue(messageData, "Минимальная зарплата не может превышать максимальную");
            return;
        }

        filters.setMinSalary(parameter);
        settingsService.save(chatId, filters);
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        var currentMinSalary = settingsService.get(messageData.getChatId()).getMinSalary();
        messageData.setText(createText(currentMinSalary));
        messageData.setKeyboardMarkup(keyboardMarkup);
    }

    private InlineKeyboardMarkup initKeyboard() {
        return SalaryFormatter.createKeyboard(SET_MIN_SALARY.getKey(), 40_000, 75_000, 100_000, 150_000);
    }
}