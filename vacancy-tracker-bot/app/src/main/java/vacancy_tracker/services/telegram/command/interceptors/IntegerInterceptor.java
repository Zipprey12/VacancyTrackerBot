package vacancy_tracker.services.telegram.command.interceptors;

import vacancy_tracker.services.util.StringUtil;

import java.util.Optional;

public class IntegerInterceptor extends InputInterceptor<Integer> {

    @Override
    protected Optional<Integer> tryCastPreparedInput(String text) {
        return StringUtil.parseInt(text);
    }
}