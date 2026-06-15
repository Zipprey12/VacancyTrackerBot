package vacancy_tracker.model.telegram.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class VacanciesShownParams {

    private boolean showIfEmpty = true;
    private boolean hasAnother = true;
}
