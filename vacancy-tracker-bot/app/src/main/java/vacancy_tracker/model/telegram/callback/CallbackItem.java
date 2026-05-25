package vacancy_tracker.model.telegram.callback;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallbackItem implements CallBackDataProvider {

    public static final char PARTS_SEPARATOR = ' ';

    private String callbackPrefix;
    private String displayedName;
    private Object key;

    public CallbackItem(String callbackPrefix, String displayedName) {
        this(callbackPrefix, displayedName, null);
    }

    @Override
    public String getText() {
        return displayedName;
    }

    @Override
    public String getCallback() {
        if (key == null) {
            return callbackPrefix;
        }
        return callbackPrefix + PARTS_SEPARATOR + key;
    }
}