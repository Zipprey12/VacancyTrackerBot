package vacancy_tracker.services.telegram.command.interceptors;

import vacancy_tracker.model.telegram.dto.CallingSource;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SettingsService;
import vacancy_tracker.services.telegram.view.formatters.TownsSelectionMessageFormatter;

public class SetTownInterceptor extends SettingInputInterceptor {

    private final TownsSelectionMessageFormatter formatter;

    public SetTownInterceptor(MessageSender sender,
                              SessionsService sessionsService,
                              SettingsService settingsService,
                              TownsSelectionMessageFormatter formatter) {
        super(sender, sessionsService, settingsService);
        this.formatter = formatter;
        setTriggerEvent(false);
        setUnsubscribeAfterPerform(false);
    }

    @Override
    protected boolean tryHandlePreparedInput(String text, long chatId) {
        var filters = settingsService.getFilters(chatId);
        var location = filters.getLocation();
        var messageData = new OutgoingMessage(MessageData.builder()
                .source(CallingSource.CHAT)
                .chatId(chatId)
                .build());

        formatter.fillMessage(messageData, location, text);
        sender.send(messageData);

        return true;
    }
}
