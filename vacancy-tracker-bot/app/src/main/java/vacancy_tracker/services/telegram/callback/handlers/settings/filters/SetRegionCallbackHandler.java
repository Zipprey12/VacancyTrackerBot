package vacancy_tracker.services.telegram.callback.handlers.settings.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.callback.CallbackData;
import vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys;
import vacancy_tracker.model.telegram.dto.LocationSearch;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.callback.handlers.NavigationCallbackHandler;
import vacancy_tracker.services.telegram.command.publishers.MessagePublisher;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.command.settings.filter.SetRegionCommand;
import vacancy_tracker.services.telegram.view.formatters.filter.RegionsSelectionMessageFormatter;

import java.util.Optional;

@Slf4j
@Component
public class SetRegionCallbackHandler extends NavigationCallbackHandler<LocationSearch> {

    public static final String KEY = FilterSettingsCallbackKeys.SET_REGION.getKey();

    private final RegionsSelectionMessageFormatter messageFormatter;
    private final MessagePublisher publisher;

    public SetRegionCallbackHandler(SetRegionCommand setRegionCommand,
                                    RegionsSelectionMessageFormatter messageFormatter,
                                    SendingAndUpdatingMessagePublisher messagePublisher) {

        super(KEY, setRegionCommand);

        this.messageFormatter = messageFormatter;
        this.publisher = messagePublisher;
    }

    @Override
    protected void navigate(MessageData message, CallbackData data) {
        var page = data.targetPage();
        var args = data.args();
        var filter = args == null ? null : args.atIndex(0).toString();
        var outgoingMessage = new OutgoingMessage(message);
        messageFormatter.fillMessage(outgoingMessage, filter, page);
        publisher.publish(outgoingMessage);
    }

    @Override
    protected Optional<LocationSearch> tryCastSelectedValue(String value) {
        return LocationSearch.parse(value);
    }
}
