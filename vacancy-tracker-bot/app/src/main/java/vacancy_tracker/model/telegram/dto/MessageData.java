package vacancy_tracker.model.telegram.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.message.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import vacancy_tracker.model.telegram.session.CallingSource;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageData {

    private Long chatId;
    private Integer messageId;
    private String text;
    private CallingSource source;

    public MessageData(MessageData source) {
        chatId = source.chatId;
        messageId = source.messageId;
        text = source.text;
        this.source = source.getSource();
    }

    public static MessageData create(MaybeInaccessibleMessage message) {
        return MessageData.builder()
                .messageId(message.getMessageId())
                .chatId(message.getChatId())
                .source(CallingSource.CALLBACK)
                .build();
    }

    public static MessageData create(Message message) {
        return MessageData.builder()
                .messageId(message.getMessageId())
                .chatId(message.getChatId())
                .text(message.getText())
                .source(CallingSource.CHAT)
                .build();
    }
}
