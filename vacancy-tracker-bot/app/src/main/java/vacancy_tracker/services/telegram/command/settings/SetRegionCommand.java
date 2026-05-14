package vacancy_tracker.services.telegram.command.settings;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.interceptors.SetRegionInterceptor;
import vacancy_tracker.services.telegram.message.MessageEditor;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.view.PaginatedKeyboardBuilder;

@Component
@Slf4j
public class SetRegionCommand extends SearchFiltersCommand {

    public static final String KEY = "/set_region";
    public static final String DESCRIPTION = "Установка региона поиска";
    public static final String HEADER_TEXT = """
            🗺️ *Выберите регион* из списка.
            Либо *введите его название* (можно часть слова) в сообщении.
            """;

    private final InlineKeyboardMarkup keyboardMarkup;

    public SetRegionCommand(MessageSender sender,
                            MessageEditor editor,
                            SessionsService sessionsService,
                            ApplicationEventPublisher eventPublisher,
                            PaginatedKeyboardBuilder regionsPaginationBuilder,
                            SetRegionInterceptor setRegionInterceptor) {

        super(KEY, DESCRIPTION,
                sender,
                editor,
                sessionsService,
                setRegionInterceptor,
                eventPublisher);

        setMarkSignificantAfterExecution(true);
        keyboardMarkup = regionsPaginationBuilder.build(0);
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        messageData.setText(HEADER_TEXT);
        messageData.setKeyboardMarkup(keyboardMarkup);
    }
}
