package vacancy_tracker.model.telegram.callback;

import lombok.Builder;

import java.util.List;

@Builder
public record CallbackData(boolean isSelection,
                           boolean isPageNavigation,
                           boolean isIgnored,
                           String prefix,
                           String selectedKey,
                           int targetPage,
                           boolean isEmpty,
                           List<String> args) {
}
