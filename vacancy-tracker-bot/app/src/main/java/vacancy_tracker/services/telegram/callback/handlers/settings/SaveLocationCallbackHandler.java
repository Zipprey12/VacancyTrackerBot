package vacancy_tracker.services.telegram.callback.handlers.settings;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys;
import vacancy_tracker.services.telegram.callback.handlers.CallbackHandler;
import vacancy_tracker.services.telegram.command.settings.SetRegionCommand;

@Component
public class SaveLocationCallbackHandler extends CallbackHandler {

    private final SetRegionCommand setLocationCommand;

    public SaveLocationCallbackHandler(SetRegionCommand setLocationCommand) {
        super(FilterSettingsCallbackKeys.LOCATION_SELECTED.getKey());
        this.setLocationCommand = setLocationCommand;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        var message = callbackQuery.getMessage();
        var messageData = MessageData.create(message);
        setLocationCommand.handleExecutionEnd(messageData, false);
    }
}
