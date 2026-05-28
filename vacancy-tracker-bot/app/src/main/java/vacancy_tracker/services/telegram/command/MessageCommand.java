package vacancy_tracker.services.telegram.command;

import vacancy_tracker.model.telegram.view.Describable;
import vacancy_tracker.services.telegram.handlers.ExecutableMessageHandler;

public interface MessageCommand extends Describable, ExecutableMessageHandler {
}
