package vacancy_tracker.services.telegram.command.settings.search;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.CompletableMessageCommand;
import vacancy_tracker.services.telegram.command.handlers.FiltersChangingCompletionHandler;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.view.keyboard.KeyboardBuilder;

import java.util.List;

import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.CANCEL_CHANGE;
import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.RESET_ALL;

@Component
public class ResetFiltersCommand extends CompletableMessageCommand {

    public static final String KEY = "/reset_filters";
    public static final String DESCRIPTION = "Сбросить фильтры для поиска вакансий";

    private static final InlineKeyboardMarkup KEYBOARD = initKeyboard();

    public ResetFiltersCommand(SendingAndUpdatingMessagePublisher publisher,
                               FiltersChangingCompletionHandler completionHandler) {
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
                new CallbackItem(RESET_ALL.getKey(), "Сбросить", true),
                new CallbackItem(CANCEL_CHANGE.getKey(), "Отмена")
        ), 2);
    }
}