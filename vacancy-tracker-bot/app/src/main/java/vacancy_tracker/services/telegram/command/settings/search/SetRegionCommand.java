package vacancy_tracker.services.telegram.command.settings.search;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.api.entity.Region;
import vacancy_tracker.model.telegram.CallingSource;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.InputInterceptingCommand;
import vacancy_tracker.services.telegram.command.handlers.FiltersChangingCompletionHandler;
import vacancy_tracker.services.telegram.command.interceptors.TextInterceptor;
import vacancy_tracker.services.telegram.command.messages.AfterRegionSelectedMessage;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.view.formatters.RegionsSelectionMessageFormatter;

@Component
@Slf4j
public class SetRegionCommand extends InputInterceptingCommand<String> {

    public static final String KEY = "/set_region";
    public static final String DESCRIPTION = "Установка региона поиска";

    private final RegionsSelectionMessageFormatter formatter;
    private final AfterRegionSelectedMessage regionSelectionUpdateMessage;

    public SetRegionCommand(SendingAndUpdatingMessagePublisher publisher,
                            FiltersChangingCompletionHandler handler,
                            RegionsSelectionMessageFormatter messageFormatter,
                            SessionsService sessionsService,
                            AfterRegionSelectedMessage regionSelectionUpdateMessage) {

        super(KEY, DESCRIPTION, publisher, handler, new TextInterceptor(), sessionsService);
        this.formatter = messageFormatter;
        this.regionSelectionUpdateMessage = regionSelectionUpdateMessage;
        setTriggerEvent(false);
    }

    public void endExecution(MessageData messageData, Region region) {
        disableInterceptor(messageData.getChatId());
        regionSelectionUpdateMessage.publish(messageData, region);
    }

    @Override
    protected void executeWithParameter(MessageData messageData, String parameter) {
        var outgoingMessage = new OutgoingMessage(MessageData.builder()
                .source(CallingSource.CHAT)
                .chatId(messageData.getChatId())
                .build());

        formatter.fillMessage(outgoingMessage, parameter);
        getPublisher().publish(outgoingMessage);
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        formatter.fillMessage(messageData);
    }
}
