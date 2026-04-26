package vacancy_tracker.model.telegram;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import vacancy_tracker.services.telegram.command.interceptors.InputInterceptor;

import javax.swing.*;

@Data
@RequiredArgsConstructor
public class UserSessionContext {

    private final long chatId;

    private InputInterceptor inputInterceptor;

    private MessageData lastSignificantMessage;

    private Action inputInterceptingAction;

    public void deleteInterceptor(){
        inputInterceptor = null;
    }
}
