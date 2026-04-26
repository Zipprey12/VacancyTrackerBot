package vacancy_tracker.services.vacancy;

import vacancy_tracker.model.api.dto.VacancySearchFilter;
import vacancy_tracker.model.api.entity.Vacancy;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface VacancyService {

    String getSourceName();

    CompletableFuture<List<Vacancy>> search(VacancySearchFilter filterDto);

    boolean isAvailable();

    int getPriority();

}
