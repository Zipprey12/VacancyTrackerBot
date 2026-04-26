package vacancy_tracker.services.telegram.command.settings;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.telegram.MessageData;
import vacancy_tracker.model.telegram.view.CallBackDataProvider;
import vacancy_tracker.model.telegram.view.FilterOptions;
import vacancy_tracker.services.telegram.command.SendingMessageCommand;
import vacancy_tracker.services.telegram.command.SupportingMessageUpdate;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.settings.SettingsService;
import vacancy_tracker.services.telegram.view.FiltersMessageFormatter;
import vacancy_tracker.services.telegram.view.KeyboardBuilder;

import java.util.Arrays;
import java.util.List;

public class SetSearchSettingsCommand extends SendingMessageCommand implements SupportingMessageUpdate {

    private final SettingsService settingsService;
    private final FiltersMessageFormatter messageFormatter;
    private final InlineKeyboardMarkup keyboardMarkup;

    public SetSearchSettingsCommand(MessageSender sender,
                                    SettingsService settingsService,
                                    FiltersMessageFormatter messageFormatter,
                                    KeyboardBuilder keyboardBuilder) {
        super("/set_filters", "Установка фильтров для поиска вакансий", sender);
        this.settingsService = settingsService;
        this.messageFormatter = messageFormatter;
        keyboardMarkup = initKeyboard(keyboardBuilder);
    }

    @Override
    public void execute(Message message) {
        var chatId = message.getChatId();
        var sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(createMessageText(chatId))
                .parseMode(ParseMode.MARKDOWN)
                .replyMarkup(keyboardMarkup)
                .build();
        sender.send(sendMessage);
    }

    @Override
    public String getMessageText(MessageData message) {
        var chatId = message.getChatId();
        return createMessageText(chatId);
    }

    @Override
    public InlineKeyboardMarkup getInlineKeyboardMarkup(MessageData message) {
        return keyboardMarkup;
    }

    private InlineKeyboardMarkup initKeyboard(KeyboardBuilder keyboardBuilder) {
        List<CallBackDataProvider> list = Arrays.asList(FilterOptions.values());
        return keyboardBuilder.buildInlineKeyboard(list, 2);
    }

    private String createMessageText(long chatId) {
        var filters = settingsService.getFilters(chatId);
        return messageFormatter.format(filters);
    }
}
