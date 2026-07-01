package vacancy_tracker.services.telegram.callback.parsers;

import vacancy_tracker.model.telegram.callback.CallbackArgs;
import vacancy_tracker.model.telegram.callback.CallbackData;
import vacancy_tracker.model.telegram.callback.CommonCallbacks;
import vacancy_tracker.services.util.StringUtil;

import static vacancy_tracker.model.telegram.callback.CallbackItem.PARTS_SEPARATOR;
import static vacancy_tracker.model.telegram.callback.CompositeCallbackItem.ARGS_PREFIX;

public class PaginationCallbackParser extends AdvancedParser {

    public static final String PAGE_PREFIX = "page_";

    public PaginationCallbackParser(String prefix) {
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

        var pageIndex = callbackData.lastIndexOf(PARTS_SEPARATOR + PAGE_PREFIX);
        var argsIndex = callbackData.indexOf(PARTS_SEPARATOR + ARGS_PREFIX);
        var firstKeyIndex = firstPositive(pageIndex, argsIndex);

        if (firstKeyIndex < 0) {
            return builder.isSelection(true)
                    .selectedKey(extractSelectedKey(callbackData, -1))
                    .build();
        } else {
            var key = extractSelectedKey(callbackData, firstKeyIndex);
            if (key == null) builder.hasEmptyKey(true);
            else {
                builder.isSelection(true);
                builder.selectedKey(key);
            }

            if (pageIndex > 0) {
                builder.isPageNavigation(true)
                        .targetPage(extractPage(callbackData, pageIndex, argsIndex));
            }
            builder.args(argsIndex > 0 ? extractArgs(callbackData, argsIndex) : null);
            return builder.build();
        }
    }

    public String createSelectPageCallback(int pageNumber) {
        return getPrefix() + PARTS_SEPARATOR + PAGE_PREFIX + pageNumber;
    }

    public String createSelectPageCallback(int pageNumber, CallbackArgs args) {
        var callbackData = createSelectPageCallback(pageNumber);
        if (args == null || args.isEmpty()) return callbackData;

        return callbackData + PARTS_SEPARATOR + ARGS_PREFIX + args;
    }

    private int extractPage(String callbackData, int pageIndex, int argsIndex) {
        var pageStart = pageIndex + 1 + PAGE_PREFIX.length();
        var pageEnd = argsIndex > 0 ? argsIndex : callbackData.length();
        return StringUtil.parseInt(callbackData.substring(pageStart, pageEnd)).orElse(0);
    }

    private int firstPositive(int a, int b) {
        if (a > 0 && b > 0) return Math.min(a, b);
        if (a > 0) return a;
        if (b > 0) return b;
        return -1;
    }
}