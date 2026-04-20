package vacancy_tracker.services.telegram.command;


import org.telegram.telegrambots.meta.api.objects.message.Message;
import vacancy_tracker.model.telegram.view.Describable;

public interface MessageBotCommand extends Describable {

    void execute(Message message);

}
