package vacancy_tracker.model.telegram.callback;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CommonCallbackKeys {

    IGNORE("ignore");

    private final String key;
}
