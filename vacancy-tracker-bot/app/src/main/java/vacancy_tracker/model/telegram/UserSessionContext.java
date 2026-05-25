package vacancy_tracker.model.telegram;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import vacancy_tracker.services.telegram.command.interceptors.InputInterceptor;

@Data
@RequiredArgsConstructor
public class UserSessionContext {

    private final long chatId;
    private InputInterceptor<?> inputInterceptor;

    public void deleteInterceptor() {
        inputInterceptor = null;
    }
}