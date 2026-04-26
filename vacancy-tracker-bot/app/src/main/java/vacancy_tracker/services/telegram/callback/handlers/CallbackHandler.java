package vacancy_tracker.services.telegram.callback.handlers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
@RequiredArgsConstructor
public abstract class CallbackHandler {

    @Getter
    private final String callbackKey;

    public abstract void handle(CallbackQuery callbackQuery);
}
