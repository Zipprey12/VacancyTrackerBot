package vacancy_tracker.model.telegram.callback;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
@Setter
public class CompositeCallbackItem extends CallbackItem {

    public static final String ARGS_PREFIX = "args_";

    private Object[] args;

    public CompositeCallbackItem(String callbackPrefix, String displayedName, Object key, Object... args) {
        super(callbackPrefix, displayedName, key);
        this.args = args;
    }

    @Override
    public String getCallback() {
        return super.getCallback() + createArgsString();
    }

    private String createArgsString() {
        if (args == null || args.length == 0) {
            return "";
        }
        return ARGS_PREFIX + Arrays.stream(args)
                .map(Object::toString)
                .collect(Collectors.joining(" "));
    }
}
