package vacancy_tracker.services.api.location;

import reactor.core.publisher.Mono;
import vacancy_tracker.model.domain.Town;

import java.util.List;

public interface TownsService {

    Mono<List<Town>> getAll();

}
