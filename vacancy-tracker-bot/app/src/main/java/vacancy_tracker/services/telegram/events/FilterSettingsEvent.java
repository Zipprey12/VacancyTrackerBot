package vacancy_tracker.services.telegram.events;

import lombok.Getter;
import vacancy_tracker.model.search.VacancySearchFilter;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.view.Identifiable;

@Getter
public class FilterSettingsEvent extends CommandExecutionEvent<Identifiable> {

    private final boolean isCanceled;
    private final transient VacancySearchFilter filter;

    public FilterSettingsEvent(Identifiable source,
                               MessageData messageData,
                               boolean isCanceled,
                               VacancySearchFilter filter) {
        super(source, messageData);
        this.isCanceled = isCanceled;
        this.filter = filter;
    }
}
