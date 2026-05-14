package vacancy_tracker.model.telegram;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.services.telegram.command.interceptors.InputInterceptor;

@Data
@RequiredArgsConstructor
public class UserSessionContext {

    private final long chatId;

    private InputInterceptor inputInterceptor;

    private MessageData lastSignificantMessage;

    public void deleteInterceptor() {
        inputInterceptor = null;
    }
}