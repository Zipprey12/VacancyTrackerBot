package vacancy_tracker.services.telegram.view.formatters.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.domain.Location;
import vacancy_tracker.model.domain.Region;
import vacancy_tracker.model.domain.Town;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.api.location.LocationsService;
import vacancy_tracker.services.mappers.CallbackItemMapper;
import vacancy_tracker.services.telegram.view.keyboard.CallbackPaginatedKeyboardBuilder;

import java.util.List;

import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.CANCEL_CHANGE;

@Component
@RequiredArgsConstructor
public class TownsSelectionMessageFormatter {

    public static final String MAIN_HEADER = """
            🗺️ *Выберите город* из списка.
            Либо *введите его название* (можно часть слова) в сообщении.
            """;

    public static final String HEADER_WITH_FILTER = "🗺️ *Выберите город* из списка.";

    public static final String REGION_NOT_SELECTED_MESSAGE =
            "Для выбора города *необходимо установить регион* поиска";

    public static final String TOWNS_NOT_FOUND = """
            Не удалось найти населенный пункты, относящиеся к данному региону.
            Поиск будет выполняться по всему региону""";

    public static final String FILTERED_TOWNS_EMPTY = """
            Не удалось найти населенные пункты по данному запросу.
            Вы можете ввести название (или его часть) для поиска еще раз
            """;

    private static final CallbackItem RESET_ITEM = new CallbackItem(CANCEL_CHANGE.getKey(),
            "Оставить только регион");

    private final LocationsService locationsService;
    private final CallbackPaginatedKeyboardBuilder townsPaginationBuilder;
    private final CallbackItemMapper mapper;

    public void fillMessage(OutgoingMessage message, Location location) {
        fillMessage(message, location, 0, null);
    }

    public void fillMessage(OutgoingMessage message, Location location, int page, String filter) {
        var towns = getTowns(location);
        if (towns.isEmpty()) {
            message.setText(location == null || location.getRegion() == null
                    ? REGION_NOT_SELECTED_MESSAGE : TOWNS_NOT_FOUND);
            return;
        }

        var filtered = filterTowns(towns, filter);
        if (filtered.isEmpty()) {
            message.setText(FILTERED_TOWNS_EMPTY);
            return;
        }

        var regionId = String.valueOf(location.getRegion().getCode());
        var keyboard = buildKeyboard(filtered, page, regionId, filter);
        message.setText(filter == null ? MAIN_HEADER : HEADER_WITH_FILTER);
        message.setKeyboardMarkup(keyboard);
    }

    private List<Town> getTowns(Location location) {
        if (location == null || location.getRegion() == null) {
            return List.of();
        }
        return locationsService.getRegionByCode(location.getRegion().getCode())
                .map(Region::getTowns)
                .filter(t -> !t.isEmpty())
                .orElse(List.of());
    }

    private List<Town> filterTowns(List<Town> towns, String filterText) {
        if (filterText == null) {
            return towns;
        }
        String low = filterText.toLowerCase();
        return towns.stream()
                .filter(t -> t.getName().toLowerCase().contains(low))
                .toList();
    }

    private InlineKeyboardMarkup buildKeyboard(List<Town> towns, int page, String regionId, String filter) {
        var items = towns.stream()
                .map(mapper::fromTown)
                .toList();
        if (filter != null) {
            return townsPaginationBuilder.build(items, page, 10,
                    List.of(regionId, filter), List.of(RESET_ITEM));
        }
        List<Object> args = regionId == null ? null : List.of(regionId);
        return townsPaginationBuilder.build(items, page, 10, args, List.of(RESET_ITEM));
    }
}
