package vacancy_tracker.model.telegram.session;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vacancy_tracker.services.telegram.command.interceptors.InputInterceptor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionContext implements Serializable {

    private long chatId;
    private String inputHandlerKey;

    @JsonIgnore
    private boolean isNew = true;

    public UserSessionContext(long chatId) {
        this.chatId = chatId;
    }

    public void setInterceptor(InputInterceptor<?> inputInterceptor) {
        if (inputInterceptor != null && inputInterceptor.getDataHandler() != null) {
            inputHandlerKey = inputInterceptor.getDataHandler().getKey();
        }
    }

    public void deleteInterceptor() {
        inputHandlerKey = null;
    }
}