package vacancy_tracker.model.telegram;

import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.message.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Data
public class MessageData {
    Long chatId;
    Integer messageId;

    public static MessageData create(MaybeInaccessibleMessage message) {
        var result = new MessageData();
        result.setMessageId(message.getMessageId());
        result.setChatId(message.getChatId());
        return result;
    }

    public static MessageData create(Message message){
        var result = new MessageData();
        result.setMessageId(message.getMessageId());
        result.setChatId(message.getChatId());
        return result;
    }
}
