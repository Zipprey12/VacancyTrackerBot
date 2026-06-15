package vacancy_tracker.services.telegram.view.formatters.filter;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.domain.Location;
import vacancy_tracker.model.search.VacancySearchFilter;
import vacancy_tracker.services.telegram.view.utils.DatesFormatUtil;

@Component
public class FiltersMessageFormatter {

    private static final String HEADER = """
            ⚙️ Настройка поиска вакансий.
            
            """;

    private static final String FOOTER = """
            
            
            _При изменении любого параметра в следующем запланированном уведомлении будут показаны все подходящие вакансии (а не только новые)._
            """;

    public String format(VacancySearchFilter filter) {
        var sb = new StringBuilder();
        appendHeader(sb);
        appendText(sb, filter.getText());
        appendLocation(sb, filter.getLocation());
        appendExperience(sb, filter.getExperience());
        appendMinSalary(sb, filter.getMinSalary());
        appendMaxSalary(sb, filter.getMaxSalary());
        sb.append(FOOTER);
        return sb.toString();
    }

    private void appendHeader(StringBuilder sb) {
        sb.append(HEADER);
    }

    private void appendText(StringBuilder sb, String text) {
        sb.append("\uD83D\uDD0D *Ключевые слова:* ");
        sb.append(text == null || text.isBlank() ? "__не задано__" : text);
    }

    private void appendLocation(StringBuilder sb, Location location) {
        sb.append("\n\uD83D\uDCCD *Местоположение:* ");
        if (location == null) {
            sb.append("__любое__");
        } else if (location.getTown() != null) {
            sb.append("г. ")
                    .append(location.getTown().getName());
        } else if (location.getRegion() != null) {
            sb.append(location.getRegion().getName());
        } else {
            sb.append("_не задано_");
        }
    }

    private void appendExperience(StringBuilder sb, Float experience) {
        sb.append("\n\uD83C\uDF93 *Опыт работы:* ");
        if (experience == null || experience == 0) {
            sb.append("_отсутствует_");
        } else {
            sb.append(DatesFormatUtil.formatYears(experience));
        }
    }

    private void appendMinSalary(StringBuilder sb, Integer minSalary) {
        sb.append("\n\uD83D\uDCB0 *Зарплата от:* ");
        if (minSalary == null || minSalary == 0) {
            sb.append("_не указана_");
        } else {
            sb.append(minSalary).append(" руб.");
        }
    }

    private void appendMaxSalary(StringBuilder sb, Integer maxSalary) {
        sb.append("\n\uD83D\uDCB5 *Зарплата до:* ");
        if (maxSalary == null || maxSalary == 0) {
            sb.append("_не указана_\n");
        } else {
            sb.append(maxSalary).append(" руб.\n");
        }
    }
}