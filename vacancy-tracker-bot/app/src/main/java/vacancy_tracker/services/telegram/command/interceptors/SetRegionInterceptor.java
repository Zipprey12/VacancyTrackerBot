package vacancy_tracker.services.telegram.command.interceptors;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import vacancy_tracker.model.api.entity.Region;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.services.telegram.mappers.CallbackItemMapper;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.session.SessionsService;
import vacancy_tracker.services.telegram.settings.SettingsService;
import vacancy_tracker.services.telegram.view.KeyboardBuilder;
import vacancy_tracker.services.vacancy.LocationsService;

import java.util.*;

//todo тут придется продумать работу с листанием регионов для каждого сообщения
@Component
public class SetRegionInterceptor extends SettingInputInterceptor {

    private final Map<String, Region> regionsByKey;
    private final CallbackItemMapper mapper;
    private final LocationsService locationsService;

    public SetRegionInterceptor(MessageSender sender,
                                SessionsService sessionsService,
                                SettingsService settingsService,
                                CallbackItemMapper callbackItemMapper,
                                LocationsService locationsService) {
        super(sender, sessionsService, settingsService);
        this.mapper = callbackItemMapper;
        this.locationsService = locationsService;
        this.regionsByKey = new LinkedHashMap<>();
        setTriggerEvent(false);
    }

    private synchronized void refreshRegions() {
        var regions = locationsService.getAllRegionsBasic();
        regionsByKey.clear();
        regions.stream()
                .sorted(Comparator.comparing(Region::getName))
                .forEach(r -> this.regionsByKey.put(r.getName().toLowerCase(), r));
    }

    //todo
    @Override
    protected boolean tryHandlePreparedInput(String text, long chatId) {
        if (regionsByKey.isEmpty()) {
            refreshRegions();
        }
        var found = searchRegions(text);
        var items = new LinkedList<CallbackItem>();
        found.forEach(r -> items.add(mapper.fromRegion(r)));

        var keyboard =  KeyboardBuilder.buildInlineKeyboard(items, 1);
        sender.send(createSendMessage(keyboard, chatId));
        return true;
    }

    private List<Region> searchRegions(String query) {
        String low = query.toLowerCase();

        return regionsByKey.entrySet()
                .stream()
                .filter(e -> e.getKey().contains(low))
                .map(Map.Entry::getValue)
                .toList();
    }

    private SendMessage createSendMessage(InlineKeyboardMarkup keyboardMarkup, long chatId) {
        return SendMessage.builder()
                .chatId(chatId)
                .text("Выберите регион: ")
                .replyMarkup(keyboardMarkup)
                .build();
    }
}
