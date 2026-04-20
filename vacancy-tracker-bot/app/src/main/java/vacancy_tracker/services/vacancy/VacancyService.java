package vacancy_tracker.services.vacancy;

import vacancy_tracker.model.vacancy.Vacancy;
import vacancy_tracker.model.vacancy.dto.VacancySearchFilterDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface VacancyService {

    String getSourceName();

    CompletableFuture<List<Vacancy>> search(VacancySearchFilterDto filterDto);

    boolean isAvailable();

    int getPriority();

}
