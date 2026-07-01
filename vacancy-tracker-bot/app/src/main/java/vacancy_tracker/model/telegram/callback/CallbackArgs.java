package vacancy_tracker.model.telegram.callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CallbackArgs {

    private final List<CallbackArg> allArgs;
    private final Map<String, CallbackArg> namedArgs;

    public CallbackArgs(List<?> rowArgs) {
        if (rowArgs == null || rowArgs.isEmpty()) {
            allArgs = List.of();
            namedArgs = Map.of();
            return;
        }
        this.allArgs = new ArrayList<>(rowArgs.size());
        namedArgs = new HashMap<>();

        for (var raw : rowArgs) {
            var arg = CallbackArg.fromString(raw.toString());
            allArgs.add(arg);
            if (arg.getKey() != null) {
                namedArgs.put(arg.getKey(), arg);
            }
        }
    }

    public boolean isEmpty() {
        return allArgs.isEmpty();
    }

    public CallbackArg getByKey(String key) {
        return namedArgs.get(key);
    }

    public List<CallbackArg> getAll() {
        return allArgs;
    }

    public int size() {
        return allArgs.size();
    }

    public CallbackArg atIndex(int index) {
        if (index >= allArgs.size() || index < 0) {
            return null;
        }
        return allArgs.get(index);
    }

    @Override
    public String toString() {
        return allArgs.stream()
                .map(Object::toString)
                .collect(Collectors.joining("&"));
    }
}
