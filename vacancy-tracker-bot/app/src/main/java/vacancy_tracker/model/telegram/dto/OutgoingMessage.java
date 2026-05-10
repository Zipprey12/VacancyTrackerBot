package vacancy_tracker.model.telegram.dto;

import lombok.*;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Getter
@Setter
public class OutgoingMessage extends MessageData {

    private InlineKeyboardMarkup keyboardMarkup;
    private String parseMode = ParseMode.MARKDOWN;

    public OutgoingMessage(MessageData source) {
        super(source);
    }
}