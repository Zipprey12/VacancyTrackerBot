package vacancy_tracker.model.telegram.callback;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import vacancy_tracker.model.telegram.view.CallBackDataProvider;

@RequiredArgsConstructor
@Getter
public class CallbackItem implements CallBackDataProvider {

    public static final char PARTS_SEPARATOR = ' ';

    private final String key;
    private final String callbackPrefix;
    private final String displayedName;

    @Override
    public String getText() {
        return displayedName;
    }

    @Override
    public String getCallback() {
        return callbackPrefix + PARTS_SEPARATOR + key;
    }
}
