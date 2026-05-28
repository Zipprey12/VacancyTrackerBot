package vacancy_tracker.model.telegram.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import vacancy_tracker.services.StringUtil;

import java.util.Optional;

@Getter
@RequiredArgsConstructor
public class LocationSearch {

    private final Integer code;
    private final String text;

    public static Optional<LocationSearch> parse(String input) {
        if (input == null || input.isBlank()) {
            return Optional.empty();
        }

        return Optional.of(StringUtil.parseInt(input.trim())
                .map(code -> new LocationSearch(code, null))
                .orElseGet(() -> new LocationSearch(null, input.trim())));
    }

    public boolean isCode() {
        return code != null;
    }

    public boolean isText() {
        return text != null;
    }
}
