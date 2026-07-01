package vacancy_tracker.services.telegram.command.settings.notification;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.command.CommandArgs;
import vacancy_tracker.model.telegram.command.CommandCategory;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.model.telegram.notification.IntervalType;
import vacancy_tracker.services.telegram.actions.settings.notification.SetDailyNotificationAction;
import vacancy_tracker.services.telegram.command.ExtendedMessageCommand;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.command.strategy.SequentialAsyncExecutionStrategy;
import vacancy_tracker.services.telegram.settings.NotificationService;
import vacancy_tracker.services.telegram.view.formatters.notification.NotificationIntervalMessageFormatter;

@Component
public class SetNotificationIntervalCommand extends ExtendedMessageCommand<IntervalType> {

    public static final String KEY = "/interval";
    public static final String DESCRIPTION = "Настроить время уведомлений";

    private final NotificationService notificationService;
    private final NotificationIntervalMessageFormatter messageFormatter;

    private final SetHoursNotificationCommand setHoursIntervalCommand;
    private final SetWeeklyNotificationCommand setWeeklyNotificationCommand;
    private final SetDailyNotificationAction setDailyNotificationAction;

    protected SetNotificationIntervalCommand(SendingAndUpdatingMessagePublisher publisher,
                                             NotificationService notificationService,
                                             NotificationIntervalMessageFormatter messageFormatter,
                                             SetHoursNotificationCommand setHoursIntervalCommand,
                                             SetWeeklyNotificationCommand setWeeklyNotificationCommand,
                                             SetDailyNotificationAction setDailyNotificationAction,
                                             SequentialAsyncExecutionStrategy strategy) {
        super(new CommandArgs(KEY, DESCRIPTION, null, CommandCategory.NOTIFICATION), publisher, strategy);
        this.notificationService = notificationService;
        this.messageFormatter = messageFormatter;
        this.setHoursIntervalCommand = setHoursIntervalCommand;
        this.setWeeklyNotificationCommand = setWeeklyNotificationCommand;
        this.setDailyNotificationAction = setDailyNotificationAction;
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
            case DAILY -> setDailyNotificationAction.execute(messageData);
            case WEEKLY -> setWeeklyNotificationCommand.execute(messageData);
        }
    }
}
