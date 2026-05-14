package vacancy_tracker.services.telegram.callback.handlers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import vacancy_tracker.model.telegram.view.Identifiable;

@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
@RequiredArgsConstructor
public abstract class CallbackHandler implements Identifiable {

    @Getter
    private final String key;

    public abstract void handle(CallbackQuery callbackQuery);
}
