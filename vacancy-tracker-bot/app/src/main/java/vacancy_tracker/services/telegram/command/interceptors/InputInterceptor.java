package vacancy_tracker.services.telegram.command.interceptors;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.command.InputHandler;

import java.util.Optional;

@RequiredArgsConstructor
public abstract class InputInterceptor<T> {

    public static final int MIN_LENGTH = 0;
    public static final int MAX_LENGTH = 100;

    @Setter
    private InputHandler<T> handler;

    protected abstract Optional<T> tryCastPreparedInput(String text, long chatId);

    protected int getMinLength() {
        return MIN_LENGTH;
    }

    protected int getMaxLength() {
        return MAX_LENGTH;
    }

    public void processInput(MessageData message) {
        var value = tryHandleInput(message.getText(), message.getChatId());
        if (value.isEmpty()) {
            handler.handleInvalidValue(message);
        } else {
            handler.handleWithParameter(message, value.get());
        }
    }

    public Optional<T> tryHandleInput(String text, long chatId) {
        if (text == null || text.isBlank()) {
            return Optional.empty();
        }

        String trimmed = text.trim();
        if (trimmed.length() < getMinLength() || trimmed.length() > getMaxLength()) {
            return Optional.empty();
        }
        return tryCastPreparedInput(trimmed, chatId);
    }
}
