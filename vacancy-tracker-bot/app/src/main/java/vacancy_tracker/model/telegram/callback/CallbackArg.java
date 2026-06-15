package vacancy_tracker.model.telegram.callback;

import lombok.Getter;


@Getter
public class CallbackArg {

    private final String key;
    private final String value;

    public CallbackArg(String key, Object value) {
        this.key = key;
        if (value == null) {
            this.value = null;
        } else this.value = value.toString();
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

    @Override
    public String toString() {
        if (key == null) {
            return value;
        }
        return key + ":" + value;
    }
}