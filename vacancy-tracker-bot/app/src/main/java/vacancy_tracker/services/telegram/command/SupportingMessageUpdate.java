package vacancy_tracker.services.telegram.command;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.telegram.MessageData;

public interface SupportingMessageUpdate {

    String getMessageText(MessageData message);

    InlineKeyboardMarkup getInlineKeyboardMarkup(MessageData message);

    default String getParseMode() {
        return ParseMode.MARKDOWN;
    }
}
