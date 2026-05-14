package vacancy_tracker.services.telegram.events;

import lombok.Getter;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.view.Identifiable;

@Getter
public class FilterSettingsEvent extends CommandExecutionEvent<Identifiable> {

    private final boolean isInterceptorUsed;

    private final boolean isCanceled;

    public FilterSettingsEvent(Identifiable source,
                               MessageData messageData,
                               boolean isInterceptorUsed,
                               boolean isCanceled) {
        super(source, messageData);
        this.isInterceptorUsed = isInterceptorUsed;
        this.isCanceled = isCanceled;
    }
}
