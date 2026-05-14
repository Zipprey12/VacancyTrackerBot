package vacancy_tracker.model.telegram.callback;

import lombok.Builder;

@Builder
public record CallbackData(boolean isSelection,
                           boolean isPageNavigation,
                           boolean isIgnored,
                           String prefix,
                           String selectedKey,
                           int targetPage,
                           boolean isEmpty,
                           String args) {
}
