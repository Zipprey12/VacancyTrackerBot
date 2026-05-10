package vacancy_tracker.services.telegram.message;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;

public interface MessageEditor {

    void edit(EditMessageText editMessageText);

    void edit(EditMessageReplyMarkup editMessageReplyMarkup);

    void edit(String text, long chatId, int messageId);

    void edit(InlineKeyboardMarkup inlineKeyboardMarkup, long chatId, int messageId);

    void edit(OutgoingMessage commandMessageData);
}
