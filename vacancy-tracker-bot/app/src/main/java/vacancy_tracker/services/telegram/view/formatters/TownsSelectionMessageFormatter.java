package vacancy_tracker.services.telegram.view.formatters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.api.entity.Location;
import vacancy_tracker.model.api.entity.Region;
import vacancy_tracker.model.api.entity.Town;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.mappers.CallbackItemMapper;
import vacancy_tracker.services.telegram.view.keyboard.PaginatedKeyboardBuilder;
import vacancy_tracker.services.vacancy.LocationsService;

import java.util.List;

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

    private final LocationsService locationsService;
    private final PaginatedKeyboardBuilder townsPaginationBuilder;
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

        var regionId = String.valueOf(location.getRegion().getId());
        var keyboard = buildKeyboard(filtered, page, regionId, filter);
        message.setText(filter == null ? MAIN_HEADER : HEADER_WITH_FILTER);
        message.setKeyboardMarkup(keyboard);
    }

    private List<Town> getTowns(Location location) {
        if (location == null || location.getRegion() == null) {
            return List.of();
        }
        return locationsService.getRegionById(location.getRegion().getId())
                .map(Region::getTowns)
                .filter(t -> !t.isEmpty())
                .orElse(List.of());
    }

    //todo Перенести логику фильтра в LocationService
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
            return townsPaginationBuilder.build(items, page, List.of(regionId, filter));
        }
        return townsPaginationBuilder.build(items, page, regionId);
    }
}
