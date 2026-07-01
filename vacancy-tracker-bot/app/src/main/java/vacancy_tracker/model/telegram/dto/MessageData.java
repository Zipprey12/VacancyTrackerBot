package vacancy_tracker.model.telegram.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import vacancy_tracker.model.telegram.session.PublishType;

import java.time.Instant;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageData {

    private Long chatId;
    private Integer messageId;
    private String text;
    private PublishType source;
    private Instant sendTime;
    private String callbackId;

    public MessageData(MessageData source) {
        chatId = source.chatId;
        messageId = source.messageId;
        text = source.text;
        this.source = source.getSource();
        sendTime = source.sendTime;
        callbackId = source.callbackId;
    }

    public static MessageData create(CallbackQuery query) {
        var message = query.getMessage();
        return MessageData.builder()
                .messageId(message.getMessageId())
                .chatId(message.getChatId())
                .source(PublishType.UPDATE)
                .sendTime(Instant.ofEpochSecond(message.getDate()))
                .callbackId(query.getId())
                .build();
    }

    public static MessageData create(Message message) {
        return MessageData.builder()
                .messageId(message.getMessageId())
                .chatId(message.getChatId())
                .text(message.getText())
                .source(PublishType.SEND)
                .sendTime(Instant.ofEpochSecond(message.getDate()))
                .build();
    }
}
