package vacancy_tracker.model.search;

import lombok.Data;
import vacancy_tracker.model.domain.Vacancy;

import java.util.ArrayList;
import java.util.List;

@Data
public class CollectedBatch {

    private final List<Vacancy> matched = new ArrayList<>();

    private long nextPage;
    private long total;
    private long nextOffset;
    private boolean isSourceExhausted;
}