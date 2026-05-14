package vacancy_tracker.services.telegram.command.settings;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.MessageDataHandlerCommand;
import vacancy_tracker.services.telegram.command.SendingAndUpdatingMessageCommand;
import vacancy_tracker.services.telegram.message.MessageEditor;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.view.KeyboardBuilder;

import java.util.List;

import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.CANCEL_FILTER_CHANGE;
import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.RESET_ALL;

public class ResetSettingsCommand extends SendingAndUpdatingMessageCommand implements MessageDataHandlerCommand {

    public static final String KEY = "/reset_filters";
    public static final String DESCRIPTION = "Сбросить фильтры для поиска вакансий";

    private static final InlineKeyboardMarkup KEYBOARD = initKeyboard();

    public ResetSettingsCommand(MessageSender sender, MessageEditor editor) {
        super(KEY, DESCRIPTION, sender, editor);
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        messageData.setText( "Вы собираетесь *сбросить* настройки поиска.\n" +
                "Это действия *нельзя отменить*. Вы уверены?");

        messageData.setKeyboardMarkup(KEYBOARD);
    }


    private static InlineKeyboardMarkup initKeyboard() {
        return KeyboardBuilder.buildInlineKeyboard(List.of(
                new CallbackItem(String.valueOf(true), RESET_ALL.getKey(), "Сбросить"),
                new CallbackItem(CANCEL_FILTER_CHANGE.getKey(), "Отмена")
        ), 2);
    }

    //todo
    @Override
    public void execute(MessageData messageData, boolean shouldOverwrite) {

    }

    //todo
    @Override
    public void handleExecutionEnd(MessageData messageData, boolean isInterceptorUsed) {

    }
}