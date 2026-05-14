package vacancy_tracker.model.telegram.callback;

import vacancy_tracker.model.telegram.view.CallBackDataProvider;

public record CallbackItem(String key, String callbackPrefix, String displayedName) implements CallBackDataProvider {

    public static final char PARTS_SEPARATOR = ' ';

    public CallbackItem(String callback, String displayedName) {
        this(null, callback, displayedName);
    }

    @Override
    public String getText() {
        return displayedName;
    }

    @Override
    public String getCallback() {
        return callbackPrefix + PARTS_SEPARATOR + key;
    }
}