package vacancy_tracker.services.telegram.command.interceptors;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.dto.CallingSource;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SettingsService;
import vacancy_tracker.services.telegram.view.formatters.RegionsSelectionMessageFormatter;

@Component
public class SetRegionInterceptor extends SettingInputInterceptor {

    private final RegionsSelectionMessageFormatter formatter;

    public SetRegionInterceptor(MessageSender sender,
                                SessionsService sessionsService,
                                SettingsService settingsService,
                                RegionsSelectionMessageFormatter formatter) {

        super(sender, sessionsService, settingsService);
        this.formatter = formatter;
        setTriggerEvent(false);
        setUnsubscribeAfterPerform(false);
    }


    @Override
    protected boolean tryHandlePreparedInput(String text, long chatId) {
        var messageData = new OutgoingMessage(MessageData.builder()
                .source(CallingSource.CHAT)
                .chatId(chatId)
                .build());

        formatter.fillMessage(messageData, text);
        sender.send(messageData);
        return true;
    }
}
