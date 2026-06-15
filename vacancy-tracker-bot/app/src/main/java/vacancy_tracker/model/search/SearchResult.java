package vacancy_tracker.model.search;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import vacancy_tracker.model.domain.RequestType;
import vacancy_tracker.model.domain.VacanciesSource;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@RequiredArgsConstructor
public class SearchResult {

    private final AtomicInteger resultsCount = new AtomicInteger(0);
    private final AtomicLong totalCount = new AtomicLong(0);
    private final AtomicBoolean hasMore = new AtomicBoolean(false);
    private final AtomicInteger notEmptyCount = new AtomicInteger(0);

    @Getter
    private final List<VacanciesResponse> vacanciesResponses = Collections.synchronizedList(new LinkedList<>());

    @Getter
    private final List<VacanciesSource> sources = Collections.synchronizedList(new LinkedList<>());

    @Getter
    private final RequestType requestType;

    @Getter
    @Setter
    private LocalDateTime modifiedFrom;

    public int getResultsCount() {
        return resultsCount.get();
    }

    public boolean hasMore() {
        return hasMore.get();
    }

    public long getTotalCount() {
        return totalCount.get();
    }

    public int getNotEmptyResponseCount() {
        return notEmptyCount.get();
    }

    public List<VacanciesSource> getNotEmptySources() {
        return vacanciesResponses.stream()
                .filter(VacanciesResponse::isNotEmpty)
                .map(VacanciesResponse::getSource)
                .toList();
    }

    public List<VacanciesResponse> getNotEmptyResponses() {
        return vacanciesResponses.stream()
                .filter(VacanciesResponse::isNotEmpty)
                .toList();
    }

    public void addResponse(VacanciesResponse response) {
        response.setRequestType(requestType);

        vacanciesResponses.add(response);
        sources.add(response.getSource());
        resultsCount.getAndAdd(response.getVacancies().size());

        if (response.isMore()) {
            hasMore.set(true);
        }
        if (response.isNotEmpty()) {
            notEmptyCount.incrementAndGet();
        }
        totalCount.getAndAdd(response.getTotal());
    }
}
