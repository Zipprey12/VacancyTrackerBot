package vacancy_tracker.services.telegram.command.interceptors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.command.InputHandler;

import java.util.Optional;

@RequiredArgsConstructor
public abstract class InputInterceptor<T> {

    public static final int MIN_LENGTH = 1;
    public static final int MAX_LENGTH = 100;

    @Setter
    @Getter
    private InputHandler<T> handler;

    protected abstract Optional<T> tryCastPreparedInput(String text);

    protected int getMinLength() {
        return MIN_LENGTH;
    }

    protected int getMaxLength() {
        return MAX_LENGTH;
    }

    public void processInput(MessageData message) {
        var value = tryHandleInput(message.getText());
        if (value.isEmpty()) {
            handler.handleInvalidValue(message);
        } else {
            handler.handleWithParameter(message, value.get());
        }
    }

    protected Optional<T> tryHandleInput(String text) {
        if (text == null || text.isBlank()) {
            return Optional.empty();
        }

        String trimmed = text.trim();
        if (trimmed.length() < getMinLength() || trimmed.length() > getMaxLength()) {
            return Optional.empty();
        }
        return tryCastPreparedInput(trimmed);
    }
}
