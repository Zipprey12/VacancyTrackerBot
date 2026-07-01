package vacancy_tracker.services.telegram.callback.handlers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import vacancy_tracker.model.telegram.view.Identifiable;

import java.util.function.Consumer;

@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
@RequiredArgsConstructor
public abstract class CallbackHandler implements Identifiable {

    @Getter
    private final String key;
    @Setter
    private Consumer<String> answerCallback;
    @Setter
    private boolean callFinish = true;

    protected abstract void handle(CallbackQuery callbackQuery);

    public void execute(CallbackQuery query) {
        handle(query);
        if (callFinish) {
            finish(query.getId());
        }
    }

    protected void finish(String callbackId) {
        if (answerCallback != null && callbackId != null) {
            answerCallback.accept(callbackId);
        }
    }
}
