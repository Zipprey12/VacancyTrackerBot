package vacancy_tracker.services.telegram.view.formatters;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.api.entity.Region;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.services.telegram.mappers.CallbackItemMapper;
import vacancy_tracker.services.telegram.view.keyboard.PaginatedKeyboardBuilder;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class RegionsSelectionMessageFormatter {

    public static final String MAIN_HEADER = """
            🗺️ *Выберите регион* из списка.
            Либо *введите его название* (можно часть слова) в сообщении.
            """;

    public static final String HEADER_WITH_FILTER = "🗺️ *Выберите регион* из списка.";

    public static final String REGIONS_NOT_FOUND = """
            Не удалось загрузить список регионов.
            Попробуйте позже.""";

    public static final String FILTERED_REGIONS_EMPTY = """
            Не удалось найти подходящие регионы.
            Вы можете ввести название (или его часть) для поиска еще раз
            """;

    private final Map<String, Region> regionsByKey;
    private final PaginatedKeyboardBuilder regionsPaginationBuilder;
    private final CallbackItemMapper mapper;

    @Getter
    private List<Region> regions;

    @Getter
    private InlineKeyboardMarkup allRegionsKeyboard;

    public RegionsSelectionMessageFormatter(List<Region> regions,
                                            PaginatedKeyboardBuilder regionsPaginationBuilder,
                                            CallbackItemMapper mapper) {


        this.regionsPaginationBuilder = regionsPaginationBuilder;
        this.mapper = mapper;
        this.regionsByKey = new LinkedHashMap<>();
        setRegions(regions);
    }

    public void setRegions(List<Region> regions) {
        this.regions = List.copyOf(regions);
        regionsByKey.clear();
        regions.stream()
                .sorted(Comparator.comparing(Region::getName))
                .forEach(r -> this.regionsByKey.put(r.getName().toLowerCase(), r));
        this.allRegionsKeyboard = createKeyboard(regions, null, 0);
    }

    public void fillMessage(OutgoingMessage message) {
        fillMessage(message, null, 0);
    }

    public void fillMessage(OutgoingMessage message, String filter) {
        fillMessage(message, filter, 0);
    }

    public void fillMessage(OutgoingMessage message, String filter, int page) {
        if (regionsByKey.isEmpty()) {
            message.setText(REGIONS_NOT_FOUND);
            return;
        }

        var filteredRegions = filterRegions(filter);
        if (filteredRegions.isEmpty()) {
            message.setText(FILTERED_REGIONS_EMPTY);
            return;
        }
        fillRegions(filteredRegions, message, filter, page);
    }

    private List<Region> filterRegions(String filterText) {
        if (filterText == null) {
            return regions;
        }
        String low = filterText.toLowerCase();
        return regions.stream()
                .filter(r -> r.getName().toLowerCase().contains(low))
                .toList();
    }

    private void fillRegions(List<Region> regions, OutgoingMessage message, String filter, int page) {
        var keyboard = createKeyboard(regions, filter, page);
        message.setText(filter == null ? MAIN_HEADER : HEADER_WITH_FILTER);
        message.setKeyboardMarkup(keyboard);
    }

    private InlineKeyboardMarkup createKeyboard(List<Region> regions, String filter, int page) {
        var items = regions.stream()
                .map(mapper::fromRegion)
                .toList();
        return regionsPaginationBuilder.build(items, page, filter);
    }
}
