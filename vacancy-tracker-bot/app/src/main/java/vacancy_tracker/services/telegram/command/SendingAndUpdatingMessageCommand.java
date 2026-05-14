package vacancy_tracker.services.telegram.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.view.Describable;
import vacancy_tracker.services.telegram.message.MessageEditor;
import vacancy_tracker.services.telegram.message.MessageSender;

@Getter(AccessLevel.PROTECTED)
@RequiredArgsConstructor
public abstract class SendingAndUpdatingMessageCommand implements MessageBotCommand, Describable {

    private final String key;
    private final String description;

    protected final MessageSender sender;
    protected final MessageEditor editor;

    //Выполняет основную логику команды и заполняет сообщение данными для отправки/редактирования
    protected abstract void executeAndPopulateMessage(OutgoingMessage messageData);

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void processInput(MessageData message, boolean shouldOverwrite) {
        var commandMessage = new OutgoingMessage(message);
        executeAndPopulateMessage(commandMessage);

        if (shouldOverwrite) {
            edit(commandMessage);
        } else {
            send(commandMessage);
        }
    }

    protected void edit(OutgoingMessage messageData){
        editor.edit(messageData);
    }

    protected void send(OutgoingMessage messageData){
        sender.send(messageData);
    }
}
