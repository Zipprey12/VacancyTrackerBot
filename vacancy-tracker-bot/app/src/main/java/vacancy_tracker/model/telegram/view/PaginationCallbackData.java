package vacancy_tracker.model.telegram.view;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PaginationCallbackData {

    private final boolean isSelection;
    private final boolean isPageNavigation;
    private final boolean isIgnored;
    private final String prefix;
    private final String selectedKey;
    private final int targetPage;
    private final boolean isEmpty;
    private final String args;
}
