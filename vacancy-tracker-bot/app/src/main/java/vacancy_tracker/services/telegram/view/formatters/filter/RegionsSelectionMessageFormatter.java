package vacancy_tracker.services.telegram.view.formatters.filter;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.domain.Region;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.model.telegram.settings.ResetFilterFieldType;
import vacancy_tracker.services.mappers.CallbackItemMapper;
import vacancy_tracker.services.telegram.view.keyboard.CallbackPaginatedKeyboardBuilder;
import vacancy_tracker.services.telegram.view.keyboard.KeyboardBuilder;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.CANCEL_CHANGE;
import static vacancy_tracker.model.telegram.callback.FilterSettingsCallbackKeys.RESET;

@Slf4j
@Component
public class RegionsSelectionMessageFormatter {

    public static final String MAIN_HEADER = """
            🗺️ *Выберите регион* из списка или *отправьте сообщением* его *код*.
            Вы также можете *отправить его название* (можно часть слова) для фильтрации.
            """;

    public static final String HEADER_WITH_FILTER = "🗺️ *Выберите регион* из списка.";

    public static final String REGIONS_NOT_FOUND = """
            Не удалось загрузить список регионов.
            Попробуйте позже.""";

    public static final String FILTERED_REGIONS_EMPTY = """
            Не удалось найти подходящие регионы.
            Вы можете ввести название (или его часть) для поиска еще раз
            """;

    private static final List<CallbackItem> BOTTOM_BUTTONS = List.of(
            new CallbackItem(CANCEL_CHANGE.getKey(), "Оставить текущий"),
            new CallbackItem(RESET.getKey(), "Сбросить", ResetFilterFieldType.LOCATION)
    );

    private static final InlineKeyboardMarkup EMPTY_ITEMS_KEYBOARD =
            KeyboardBuilder.buildInlineKeyboard(BOTTOM_BUTTONS, 2);

    private final Map<String, Region> regionsByKey;
    private final CallbackPaginatedKeyboardBuilder regionsPaginationBuilder;
    private final CallbackItemMapper mapper;

    @Getter
    private List<Region> regions;

    @Getter
    private InlineKeyboardMarkup allRegionsKeyboard;

    public RegionsSelectionMessageFormatter(CallbackPaginatedKeyboardBuilder regionsPaginationBuilder,
                                            CallbackItemMapper mapper) {


        this.regionsPaginationBuilder = regionsPaginationBuilder;
        this.mapper = mapper;
        this.regionsByKey = new LinkedHashMap<>();
    }

    public void setRegions(List<Region> regions) {
        log.info("REGIONS: {}", regions.size());
        this.regions = List.copyOf(regions)
                .stream()
                .sorted(Comparator.comparing(Region::getName))
                .toList();

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
            fillEmptyRegionsMessage(message);
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

    private void fillEmptyRegionsMessage(OutgoingMessage message) {
        message.setText(FILTERED_REGIONS_EMPTY);
        message.setKeyboardMarkup(EMPTY_ITEMS_KEYBOARD);
    }

    private InlineKeyboardMarkup createKeyboard(List<Region> regions, String filter, int page) {
        var items = regions.stream()
                .map(mapper::fromRegion)
                .toList();
        List<Object> args = filter == null ? null : List.of(filter);
        return regionsPaginationBuilder.build(items, page, 10, args, BOTTOM_BUTTONS);
    }
}