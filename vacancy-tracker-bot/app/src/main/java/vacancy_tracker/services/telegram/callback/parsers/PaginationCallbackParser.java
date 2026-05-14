package vacancy_tracker.services.telegram.callback.parsers;

import vacancy_tracker.model.telegram.callback.CommonCallbackKeys;
import vacancy_tracker.model.telegram.callback.CallbackData;
import vacancy_tracker.services.StringUtil;

import static vacancy_tracker.model.telegram.callback.CallbackItem.PARTS_SEPARATOR;

public class PaginationCallbackParser extends CallbackParser {

    public static final String PAGE_PREFIX = "page_";
    public static final String ARGS_PREFIX = "args_";

    public PaginationCallbackParser(String prefix) {
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

        int pageIndex = callbackData.lastIndexOf(PARTS_SEPARATOR + PAGE_PREFIX);
        if (pageIndex <= 0) {
            return builder.isSelection(true)
                    .selectedKey(callbackData.substring(getPrefix().length() + 1))
                    .build();

        }

        int pageStart = pageIndex + 1 + PAGE_PREFIX.length();
        int argsIndex = callbackData.indexOf(PARTS_SEPARATOR + ARGS_PREFIX, pageStart);
        int pageEnd = argsIndex >= 0 ? argsIndex : callbackData.length();
        int argsStart = argsIndex >= 0 ? argsIndex + 1 + ARGS_PREFIX.length() : -1;
        int targetPage = StringUtil.parseInt(callbackData.substring(pageStart, pageEnd))
                .orElse(0);

        return builder.isPageNavigation(true)
                .prefix(callbackData.substring(0, pageIndex))
                .targetPage(targetPage)
                .args(argsStart >= 0 ? callbackData.substring(argsStart) : null)
                .build();
    }

    public String createSelectPageCallback(int pageNumber) {
        return getPrefix() + PARTS_SEPARATOR + PAGE_PREFIX + pageNumber;
    }

    public String createSelectPageCallback(int pageNumber, String args) {
        var callbackData = createSelectPageCallback(pageNumber);
        if (args == null || args.isBlank()) {
            return callbackData;
        }
        return callbackData + PARTS_SEPARATOR + ARGS_PREFIX + args;
    }
}