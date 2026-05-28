package vacancy_tracker.services.telegram.command.settings.notification;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.IntervalType;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.ExtendedMessageCommand;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.settings.NotificationService;
import vacancy_tracker.services.telegram.view.formatters.notification.NotificationIntervalMessageFormatter;

@Component
public class SetNotificationIntervalCommand extends ExtendedMessageCommand<IntervalType> {

    public static final String KEY = "/set_interval";
    public static final String DESCRIPTION = "Задать интервал отправки уведомлений";

    private final NotificationService notificationService;
    private final NotificationIntervalMessageFormatter messageFormatter;

    private final SetHoursNotificationCommand setHoursIntervalCommand;
    private final SetWeeklyNotificationCommand setWeeklyNotificationCommand;
    private final SetDailyNotificationCommand setDailyNotificationCommand;

    protected SetNotificationIntervalCommand(SendingAndUpdatingMessagePublisher publisher,
                                             NotificationService notificationService,
                                             NotificationIntervalMessageFormatter messageFormatter,
                                             SetHoursNotificationCommand setHoursIntervalCommand,
                                             SetWeeklyNotificationCommand setWeeklyNotificationCommand,
                                             SetDailyNotificationCommand setDailyNotificationCommand) {
        super(KEY, DESCRIPTION, publisher);
        this.notificationService = notificationService;
        this.messageFormatter = messageFormatter;
        this.setHoursIntervalCommand = setHoursIntervalCommand;
        this.setWeeklyNotificationCommand = setWeeklyNotificationCommand;
        this.setDailyNotificationCommand = setDailyNotificationCommand;
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        var settings = notificationService.get(messageData.getChatId());
        messageFormatter.fill(messageData, settings);
    }

    @Override
    protected void executeWithParameters(MessageData messageData, IntervalType parameter) {
        switch (parameter) {
            case HOURS -> setHoursIntervalCommand.execute(messageData);
            case DAILY -> setDailyNotificationCommand.execute(messageData);
            case WEEKLY -> setWeeklyNotificationCommand.execute(messageData);
        }
    }
}
