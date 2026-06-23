package vacancy_tracker.model.telegram.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Getter
@Setter
@NoArgsConstructor
public class OutgoingMessage extends MessageData {

    private InlineKeyboardMarkup keyboardMarkup;
    private String parseMode = ParseMode.MARKDOWN;
    private boolean sendIfNotLast = false;

    public OutgoingMessage(MessageData source) {
        super(source);
    }

    public OutgoingMessage copy(OutgoingMessage message) {
        var out = new OutgoingMessage(message);
        out.setKeyboardMarkup(message.getKeyboardMarkup());
        out.setParseMode(message.getParseMode());
        out.setSendIfNotLast(message.isSendIfNotLast());
        return out;
    }
}