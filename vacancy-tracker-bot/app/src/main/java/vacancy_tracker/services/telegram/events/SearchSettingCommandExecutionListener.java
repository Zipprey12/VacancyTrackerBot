package vacancy_tracker.services.telegram.events;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import vacancy_tracker.services.telegram.command.settings.SetSearchSettingsCommand;
import vacancy_tracker.services.telegram.message.DefaultMessageEditor;

@Component
@RequiredArgsConstructor
public class SearchSettingCommandExecutionListener {

    private final SetSearchSettingsCommand command;
    private final DefaultMessageEditor editor;

    @Async
    @EventListener
    public void handleSettingCommandExecutionEvent(SettingCommandExecutionEvent commandExecutionEvent){
        var data = commandExecutionEvent.getMessageData();
        var chatId = data.getChatId();
        var messageId = data.getMessageId();

        var keyboard = command.getInlineKeyboardMarkup(data);
        String messageText = command.getMessageText(data);

        EditMessageText editText = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(messageText)
                .parseMode(ParseMode.MARKDOWN)
                .build();

        EditMessageReplyMarkup replyMarkup = EditMessageReplyMarkup.builder()
                .chatId(chatId)
                .messageId(messageId)
                .replyMarkup(keyboard)
                .build();

        editor.edit(editText);
        editor.edit(replyMarkup);
    }
}
