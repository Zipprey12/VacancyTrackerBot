package vacancy_tracker.model.telegram.callback;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public class CallbackArg {

    private final String key;
    private final String value;

    @Override
    public String toString() {
        if (key == null) {
            return value;
        }
        return key + ":" + value;
    }

    public static CallbackArg fromString(String text) {
        if (text == null || text.isEmpty()) {
            return new CallbackArg(null, null);
        }
        int index = text.indexOf(':');
        if (index <= 0 || index == text.length() - 1) {
            return new CallbackArg(null, text);
        }
        var key = text.substring(0, index);
        var value = text.substring(index + 1);
        return new CallbackArg(key, value);
    }
}