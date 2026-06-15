package vacancy_tracker.services.telegram.command.interceptors;

import vacancy_tracker.services.util.StringUtil;

import java.time.LocalTime;
import java.util.Optional;

public class TimeInterceptor extends InputInterceptor<LocalTime> {

    @Override
    protected Optional<LocalTime> tryCastPreparedInput(String text) {
        return StringUtil.parseTime(text);
    }
}
