package vacancy_tracker.services.telegram.callback.parsers;

import vacancy_tracker.model.telegram.callback.CallbackData;
import vacancy_tracker.model.telegram.callback.CommonCallbackKeys;

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

        if (CommonCallbackKeys.IGNORE.getKey().equals(callbackData)) {
            return builder.isIgnored(true).build();
        }
        if (callbackData.equals(getPrefix())) {
            return builder.isEmpty(true).build();
        }

        var args = extractArgs(callbackData);
        return builder.isSelection(true)
                .selectedKey(extractSelectedKey(callbackData, -1))
                .args(args.isEmpty() ? null : args)
                .build();
    }

    protected String extractSelectedKey(String callbackData, int endIndex) {
        var valueStart = getPrefix().length() + 1;
        var argsIndex = callbackData.indexOf(PARTS_SEPARATOR + ARGS_PREFIX);
        var valueEnd = resolveValueEnd(callbackData, endIndex, argsIndex);

        if (valueStart >= valueEnd) return null;
        return callbackData.substring(valueStart, valueEnd);
    }

    protected List<String> extractArgs(String callbackData) {
        var argsIndex = callbackData.indexOf(PARTS_SEPARATOR + ARGS_PREFIX);
        if (argsIndex <= 0) return List.of();
        var argsStart = argsIndex + 1 + ARGS_PREFIX.length();
        return splitArgs(callbackData.substring(argsStart));
    }

    protected List<String> splitArgs(String argsString) {
        if (argsString == null || argsString.isEmpty()) return List.of();
        return List.of(argsString.split("_"));
    }

    private int resolveValueEnd(String callbackData, int endIndex, int argsIndex) {
        if (endIndex > 0) return endIndex;
        if (argsIndex > 0) return argsIndex;
        return callbackData.length();
    }
}
