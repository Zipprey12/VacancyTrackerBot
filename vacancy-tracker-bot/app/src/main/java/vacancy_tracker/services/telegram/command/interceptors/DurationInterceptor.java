package vacancy_tracker.services.telegram.command.interceptors;

import vacancy_tracker.services.util.StringUtil;

import java.time.Duration;
import java.util.Optional;

public class DurationInterceptor extends InputInterceptor<Duration> {

    @Override
    protected Optional<Duration> tryCastPreparedInput(String text) {
        return StringUtil.parseDuration(text);
    }
}
