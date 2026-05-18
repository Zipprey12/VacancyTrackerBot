package vacancy_tracker.services.telegram.command.interceptors;

import java.util.Optional;

public class TextInterceptor extends InputInterceptor<String> {

    @Override
    protected Optional<String> tryCastPreparedInput(String text, long chatId) {
        return Optional.of(text);
    }
}
