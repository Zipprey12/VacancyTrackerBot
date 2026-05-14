package vacancy_tracker.services.telegram.command.settings;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.api.entity.Region;
import vacancy_tracker.model.api.entity.Town;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.command.interceptors.SetTownInterceptor;
import vacancy_tracker.services.telegram.mappers.CallbackItemMapper;
import vacancy_tracker.services.telegram.message.MessageEditor;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SettingsService;
import vacancy_tracker.services.telegram.view.PaginatedKeyboardBuilder;
import vacancy_tracker.services.vacancy.LocationsService;

import java.util.LinkedList;
import java.util.List;

@Component
public class SetTownCommand extends SearchFiltersCommand {

    public static final String KEY = "/set_town";
    public static final String DESCRIPTION = "Установка города поиска";
    private static final String HEADER_TEXT = """
            🗺️ *Выберите город* из списка.
            Либо *введите его название* (можно часть слова) в сообщении.
            """;

    private static final String REGION_NOT_SELECTED_MESSAGE =
            "Для выбора города *необходимо установить регион* поиска";

    public static final String TOWNS_NOT_FOUND = """
            Не удалось найти населенный пункты, относящиеся к данному региону.
            Поиск будет выполняться по всему региону""";

    private final SettingsService settingsService;
    private final LocationsService locationsService;
    private final PaginatedKeyboardBuilder keyboardBuilder;
    private final CallbackItemMapper mapper;

    public SetTownCommand(MessageSender sender,
                          MessageEditor editor,
                          SessionsService sessionsService,
                          ApplicationEventPublisher eventPublisher,
                          SettingsService settingsService,
                          PaginatedKeyboardBuilder townsPaginationBuilder,
                          LocationsService locationsService,
                          CallbackItemMapper mapper) {
        super(KEY, DESCRIPTION,
                sender,
                editor,
                sessionsService,
                new SetTownInterceptor(sender, sessionsService, settingsService),
                eventPublisher);

        this.settingsService = settingsService;
        this.locationsService = locationsService;
        this.keyboardBuilder = townsPaginationBuilder;
        this.mapper = mapper;

        setMarkSignificantAfterExecution(true);
    }

    @Override
    protected void executeAndPopulateMessage(OutgoingMessage messageData) {
        var settings = settingsService.getFilters(messageData.getChatId());
        var location = settings.getLocation();
        Region region;

        if (location == null || (region = location.getRegion()) == null) {
            messageData.setText(REGION_NOT_SELECTED_MESSAGE);
            return;
        }

        var foundRegion = locationsService.getRegionById(region.getId());
        if (foundRegion.isEmpty()) {
            messageData.setText(TOWNS_NOT_FOUND);
            return;
        }
        var towns = foundRegion.get().getTowns();
        if (towns == null || towns.isEmpty()) {
            messageData.setText(TOWNS_NOT_FOUND);
            return;
        }
        fillValidMessageData(towns, String.valueOf(region.getId()), messageData);
    }

    private void fillValidMessageData(List<Town> towns, String regionId, OutgoingMessage message) {
        var keyboard = buildKeyboard(towns, regionId);
        message.setText(HEADER_TEXT);
        message.setKeyboardMarkup(keyboard);
    }


    private InlineKeyboardMarkup buildKeyboard(List<Town> towns, String regionId) {
        var items = new LinkedList<CallbackItem>();
        towns.forEach(t -> items.add(mapper.fromTown(t)));
        keyboardBuilder.setItems(items);

        return keyboardBuilder.build(0, regionId);
    }
}
