package vacancy_tracker.model.telegram.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.message.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageData {

    Long chatId;
    Integer messageId;
    String text;

    public static MessageData create(MaybeInaccessibleMessage message) {
        var result = new MessageData();
        result.setMessageId(message.getMessageId());
        result.setChatId(message.getChatId());
        return result;
    }

    public static MessageData create(Message message) {
        var result = new MessageData();
        result.setMessageId(message.getMessageId());
        result.setChatId(message.getChatId());
        result.setText(message.getText());
        return result;
    }

    public MessageData(MessageData source) {
        this.chatId = source.chatId;
        this.messageId = source.messageId;
        this.text = source.text;
    }
}
