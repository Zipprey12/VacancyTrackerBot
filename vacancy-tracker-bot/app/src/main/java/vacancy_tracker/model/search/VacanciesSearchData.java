package vacancy_tracker.model.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VacanciesSearchData {

    private long chatId;
    private Integer messageId;
    private VacancySearchFilter filter;
    private int limit;
    private int page;
}
