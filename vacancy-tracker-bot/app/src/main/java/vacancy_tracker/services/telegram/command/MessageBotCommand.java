package vacancy_tracker.services.telegram.command;


import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.view.Describable;

public interface MessageBotCommand extends Describable {

    void execute(MessageData message, boolean shouldOverwrite);

}
