package vacancy_tracker.services.telegram.command.interceptors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.handlers.IdentifiableDataHandler;
import vacancy_tracker.services.telegram.handlers.InputErrorHandler;

import java.util.Optional;

@RequiredArgsConstructor
public abstract class InputInterceptor<T> {

    public static final int MIN_LENGTH = 1;
    public static final int MAX_LENGTH = 100;
    public static final String TOO_SHORT_MESSAGE = "Отправлено слишком короткое сообщение";
    public static final String TOO_LONG_MESSAGE = "Отправлено слишком длинное сообщение";

    @Setter
    @Getter
    private IdentifiableDataHandler<T> dataHandler;

    @Getter
    @Setter
    private InputErrorHandler errorHandler;

    protected abstract Optional<T> tryCastPreparedInput(String text);

    protected int getMinLength() {
        return MIN_LENGTH;
    }

    protected int getMaxLength() {
        return MAX_LENGTH;
    }

    public void processInput(MessageData message) {
        var value = tryHandleInput(message);
        if (value.isEmpty()) {
            errorHandler.handleInvalidValue(message);
        } else {
            dataHandler.handleWithParameter(message, value.get());
        }
    }

    protected Optional<T> tryHandleInput(MessageData message) {
        var text = message.getText();
        if (text == null || text.isBlank()) {
            return Optional.empty();
        }

        String trimmed = text.trim();
        if (trimmed.length() < getMinLength()) {
            errorHandler.handleInvalidValue(message, TOO_SHORT_MESSAGE);
            return Optional.empty();
        }
        if (trimmed.length() > getMaxLength()) {
            errorHandler.handleInvalidValue(message, TOO_LONG_MESSAGE);
            return Optional.empty();
        }
        return tryCastPreparedInput(trimmed);
    }
}
