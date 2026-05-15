package vacancy_tracker.services.telegram.command.settings;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.InputInterceptingCommand;
import vacancy_tracker.services.telegram.command.handlers.FiltersInterceptingCompletionHandler;
import vacancy_tracker.services.telegram.command.interceptors.SetRegionInterceptor;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.view.formatters.RegionsSelectionMessageFormatter;

@Component
@Slf4j
public class SetRegionCommand extends InputInterceptingCommand {

    public static final String KEY = "/set_region";
    public static final String DESCRIPTION = "Установка региона поиска";

    private final RegionsSelectionMessageFormatter formatter;

    public SetRegionCommand(SendingAndUpdatingMessagePublisher publisher,
                            FiltersInterceptingCompletionHandler handler,
                            RegionsSelectionMessageFormatter messageFormatter,
                            SetRegionInterceptor setRegionInterceptor,
                            SessionsService sessionsService) {

        super(KEY, DESCRIPTION, publisher, handler, setRegionInterceptor, sessionsService);
        this.formatter = messageFormatter;
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        formatter.fillMessage(messageData);
    }
}
