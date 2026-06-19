package vacancy_tracker.services.telegram.command.settings.notification;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.command.CommandArgs;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.model.telegram.notification.NotificationSettings;
import vacancy_tracker.services.telegram.actions.message.AfterDayOfWeekSelectedMessage;
import vacancy_tracker.services.telegram.command.InputInterceptingCommand;
import vacancy_tracker.services.telegram.command.interceptors.IntegerInterceptor;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.command.strategy.SequentialAsyncExecutionStrategy;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.NotificationService;
import vacancy_tracker.services.telegram.view.formatters.notification.WeeklyNotificationMessageFormatter;
import vacancy_tracker.services.util.DateUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class SetWeeklyNotificationCommand extends InputInterceptingCommand<Integer> {

    public static final String KEY = "set_weekly_notification";

    private final NotificationService service;
    private final WeeklyNotificationMessageFormatter messageFormatter;
    private final AfterDayOfWeekSelectedMessage afterDayOfWeekSelectedMessage;

    protected SetWeeklyNotificationCommand(SendingAndUpdatingMessagePublisher publisher,
                                           SessionsService sessionsService,
                                           NotificationService service,
                                           WeeklyNotificationMessageFormatter messageFormatter,
                                           AfterDayOfWeekSelectedMessage afterDayOfWeekSelectedMessage,
                                           SequentialAsyncExecutionStrategy strategy) {
        super(new CommandArgs(KEY, null, null), publisher,
                new IntegerInterceptor(), sessionsService, strategy);
        this.service = service;
        this.messageFormatter = messageFormatter;
        this.afterDayOfWeekSelectedMessage = afterDayOfWeekSelectedMessage;
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        var settings = service.get(messageData.getChatId());
        messageFormatter.format(messageData, settings);
    }

    @Override
    protected void executeWithParameters(MessageData messageData, Integer parameter) {
        if (parameter < 1 || parameter > 7) {
            handleInvalidValue(messageData, "Номер дня недели должен быть от 1 до 7");
            return;
        }

        var chatId = messageData.getChatId();
        var settings = service.get(chatId);
        var currentDateTime = settings.getNextNotificationAt();
        var time = currentDateTime == null ? LocalTime.NOON : currentDateTime.toLocalTime();
        var next = DateUtil.nextDayOfWeek(LocalDateTime.now(), parameter, time);
        settings.setNextNotificationAt(next);
        settings.setInterval(Duration.ofDays(7));
        service.save(chatId, settings);
        sendNext(messageData, settings);
    }

    private void sendNext(MessageData messageData, NotificationSettings settings) {
        var outgoingMessage = new OutgoingMessage(messageData);
        afterDayOfWeekSelectedMessage.format(outgoingMessage, settings);
        getPublisher().publish(outgoingMessage);
    }
}