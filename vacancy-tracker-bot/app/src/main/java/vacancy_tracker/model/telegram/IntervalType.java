package vacancy_tracker.model.telegram;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum IntervalType {

    HOURS("hours"),
    DAILY("daily"),
    WEEKLY("weekly");

    private final String key;
}
