package vacancy_tracker.model.search;

import java.util.function.BiConsumer;

public record SearchOutcome(SearchResult result, BiConsumer<Integer, Integer> onPublished) {

    public static SearchOutcome withoutCallback(SearchResult result) {
        return new SearchOutcome(result, (messageId, nextPage) -> {
        });
    }
}