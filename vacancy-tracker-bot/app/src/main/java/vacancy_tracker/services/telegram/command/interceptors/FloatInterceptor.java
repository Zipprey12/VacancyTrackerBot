package vacancy_tracker.services.telegram.command.interceptors;

import vacancy_tracker.services.util.StringUtil;

import java.util.Optional;

public class FloatInterceptor extends InputInterceptor<Float> {

    @Override
    protected Optional<Float> tryCastPreparedInput(String text) {
        return StringUtil.parseFloat(text);
    }
}
