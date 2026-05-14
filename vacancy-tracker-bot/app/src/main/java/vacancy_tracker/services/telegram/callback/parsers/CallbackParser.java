package vacancy_tracker.services.telegram.callback.parsers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import vacancy_tracker.model.telegram.callback.CallbackItem;
import vacancy_tracker.model.telegram.callback.CommonCallbackKeys;
import vacancy_tracker.model.telegram.callback.CallbackData;

@RequiredArgsConstructor
public class CallbackParser {

    @Getter(AccessLevel.PROTECTED)
    private final String prefix;

    public CallbackData parse(String callbackData) {
        var builder = CallbackData.builder();

        if (CommonCallbackKeys.IGNORE.getKey().equals(callbackData)) {
            return builder.isIgnored(true).build();
        }

        if (callbackData.equals(prefix)) {
            return builder.isEmpty(true).build();
        }

        var key = getKey(callbackData);
        return builder.isSelection(true)
                .selectedKey(key)
                .build();
    }

    public String createSelectItemCallback(CallbackItem item) {
        return item.getCallback();
    }

    private String getKey(String callback) {
        return callback.substring(prefix.length() + 1);
    }
}
