package vacancy_tracker.services.telegram.callback.parsers;

import vacancy_tracker.model.telegram.callback.CallbackArgs;
import vacancy_tracker.model.telegram.callback.CallbackData;
import vacancy_tracker.model.telegram.callback.CommonCallbacks;

import java.util.List;

import static vacancy_tracker.model.telegram.callback.CallbackItem.PARTS_SEPARATOR;
import static vacancy_tracker.model.telegram.callback.CompositeCallbackItem.ARGS_PREFIX;

public class AdvancedParser extends CallbackParser {

    public AdvancedParser(String prefix) {
        super(prefix);
    }

    @Override
    public CallbackData parse(String callbackData) {
        var builder = CallbackData.builder();
        builder.prefix(getPrefix());

        if (CommonCallbacks.IGNORE.getKey().equals(callbackData)) {
            return builder.isIgnored(true).build();
        }

        if (callbackData.equals(getPrefix())) {
            return builder.hasEmptyKey(true).build();
        }

        var argsIndex = callbackData.indexOf(PARTS_SEPARATOR + ARGS_PREFIX);
        var args = extractArgs(callbackData, argsIndex);

        var key = extractSelectedKey(callbackData, argsIndex);
        return builder.isSelection(true)
                .selectedKey(key)
                .hasEmptyKey(key == null)
                .args(args)
                .build();
    }

    protected String extractSelectedKey(String callbackData, int argsIndex) {
        var valueStart = getPrefix().length() + 1;
        var valueEnd = resolveValueEnd(callbackData, argsIndex);

        if (valueStart >= valueEnd) return null;
        return callbackData.substring(valueStart, valueEnd);
    }

    protected CallbackArgs extractArgs(String callbackData, int argsIndex) {
        if (argsIndex <= 0) return null;
        var argsStart = argsIndex + 1 + ARGS_PREFIX.length();
        var row = splitArgs(callbackData.substring(argsStart));
        return new CallbackArgs(row);
    }

    protected List<String> splitArgs(String argsString) {
        if (argsString == null || argsString.isEmpty()) return List.of();
        return List.of(argsString.split("&"));
    }

    private int resolveValueEnd(String callbackData, int argsIndex) {
        if (argsIndex > 0) return argsIndex;
        return callbackData.length();
    }
}
