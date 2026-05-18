package vacancy_tracker.model.telegram.callback;

import vacancy_tracker.model.telegram.view.CallBackDataProvider;

public record CallbackItem(String callbackPrefix, String displayedName, Object key) implements CallBackDataProvider {

    public static final char PARTS_SEPARATOR = ' ';

    public CallbackItem(String callback, String displayedName) {
        this(callback, displayedName, null);
    }

    @Override
    public String getText() {
        return displayedName;
    }

    @Override
    public String getCallback() {
        return callbackPrefix + PARTS_SEPARATOR + (key == null ? "" : key.toString());
    }
}