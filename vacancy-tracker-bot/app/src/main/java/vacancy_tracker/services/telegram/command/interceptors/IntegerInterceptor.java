package vacancy_tracker.services.telegram.command.interceptors;

import vacancy_tracker.services.StringUtil;

import java.util.Optional;

public class IntegerInterceptor extends InputInterceptor<Integer> {

    @Override
    protected Optional<Integer> tryCastPreparedInput(String text, long chatId) {
        return StringUtil.parseInt(text);
    }
}