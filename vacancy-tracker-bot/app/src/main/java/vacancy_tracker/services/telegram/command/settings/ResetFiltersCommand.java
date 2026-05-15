package vacancy_tracker.services.telegram.command.settings;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.MessageCommand;
import vacancy_tracker.services.telegram.command.handlers.FiltersCompletionHandler;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.view.KeyboardBuilder;

import java.util.List;

import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.CANCEL_FILTER_CHANGE;
import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.RESET_ALL;

@Component
public class ResetFiltersCommand extends MessageCommand {

    public static final String KEY = "/reset_filters";
    public static final String DESCRIPTION = "Сбросить фильтры для поиска вакансий";

    private static final InlineKeyboardMarkup KEYBOARD = initKeyboard();

    public ResetFiltersCommand(SendingAndUpdatingMessagePublisher publisher,
                               FiltersCompletionHandler completionHandler) {
        super(KEY, DESCRIPTION, publisher, completionHandler);
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        messageData.setText("Вы собираетесь *сбросить* настройки поиска.\n" +
                "Это действия *нельзя отменить*. Вы уверены?");

        messageData.setKeyboardMarkup(KEYBOARD);
    }


    private static InlineKeyboardMarkup initKeyboard() {
        return KeyboardBuilder.buildInlineKeyboard(List.of(
                new CallbackItem(String.valueOf(true), RESET_ALL.getKey(), "Сбросить"),
                new CallbackItem(CANCEL_FILTER_CHANGE.getKey(), "Отмена")
        ), 2);
    }
}