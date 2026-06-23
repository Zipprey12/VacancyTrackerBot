package vacancy_tracker.services.telegram.command.settings.filter;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.telegram.callback.CallBackDataProvider;
import vacancy_tracker.model.telegram.command.CommandArgs;
import vacancy_tracker.model.telegram.command.CommandCategory;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.model.telegram.settings.FilterOptions;
import vacancy_tracker.services.telegram.command.AbstractMessageCommand;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.settings.SearchFiltersService;
import vacancy_tracker.services.telegram.view.formatters.filter.FiltersMessageFormatter;
import vacancy_tracker.services.telegram.view.keyboard.KeyboardBuilder;

import java.util.Arrays;
import java.util.List;

@Component
public class SetSearchSettingsCommand extends AbstractMessageCommand {

    public static final String KEY = "/filters";
    public static final String DESCRIPTION = "Настроить фильтры";

    private final SearchFiltersService settingsService;
    private final FiltersMessageFormatter messageFormatter;
    private final InlineKeyboardMarkup keyboardMarkup;

    public SetSearchSettingsCommand(SendingAndUpdatingMessagePublisher publisher,
                                    SearchFiltersService settingsService,
                                    FiltersMessageFormatter messageFormatter) {
        super(new CommandArgs(KEY, DESCRIPTION, null, CommandCategory.MAIN), publisher);
        this.settingsService = settingsService;
        this.messageFormatter = messageFormatter;
        keyboardMarkup = initKeyboard();
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage message) {
        var chatId = message.getChatId();
        message.setKeyboardMarkup(keyboardMarkup);
        message.setText(createMessageText(chatId));
    }

    private InlineKeyboardMarkup initKeyboard() {
        List<CallBackDataProvider> list = Arrays.asList(FilterOptions.values());
        return KeyboardBuilder.buildInlineKeyboard(list, 2);
    }

    private String createMessageText(long chatId) {
        var filters = settingsService.get(chatId);
        return messageFormatter.format(filters);
    }
}
