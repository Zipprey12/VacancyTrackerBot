package vacancy_tracker.services.telegram.command.settings;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.model.telegram.view.CallBackDataProvider;
import vacancy_tracker.model.telegram.view.FilterOptions;
import vacancy_tracker.services.telegram.command.MessageCommand;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.settings.SettingsService;
import vacancy_tracker.services.telegram.view.KeyboardBuilder;
import vacancy_tracker.services.telegram.view.formatters.FiltersMessageFormatter;

import java.util.Arrays;
import java.util.List;

@Component
public class SetSearchSettingsCommand extends MessageCommand {

    private final SettingsService settingsService;
    private final FiltersMessageFormatter messageFormatter;
    private final InlineKeyboardMarkup keyboardMarkup;

    public SetSearchSettingsCommand(SendingAndUpdatingMessagePublisher publisher,
                                    SettingsService settingsService,
                                    FiltersMessageFormatter messageFormatter) {
        super("/set_filters", "Настройки поиска вакансий", publisher);
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
        var filters = settingsService.getFilters(chatId);
        return messageFormatter.format(filters);
    }
}
