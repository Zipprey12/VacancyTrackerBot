package vacancy_tracker.services.telegram.view;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.api.dto.VacancySearchFilter;
import vacancy_tracker.model.api.entity.Location;

import java.util.Objects;

@Component
public class FiltersMessageFormatter {

    private static final String HEADER = """
            ⚙️ Настройка поиска вакансий.
            
            """;

    public String format(VacancySearchFilter filter) {
        var sb = new StringBuilder();
        appendHeader(sb);
        appendText(sb, filter.getText());
        appendLocation(sb, filter.getLocation());
        appendExperience(sb, filter.getExperience());
        appendMinSalary(sb, filter.getMinSalary());
        appendMaxSalary(sb, filter.getMaxSalary());
        return sb.toString();
    }

    private void appendHeader(StringBuilder sb) {
        sb.append(HEADER);
    }

    private void appendText(StringBuilder sb, String text) {
        sb.append("\uD83D\uDD0D *Ключевые слова:* ");
        sb.append(Objects.requireNonNullElse(text, "_не задано_"));
    }

    private void appendLocation(StringBuilder sb, Location location) {
        sb.append("\n\uD83D\uDCCD *Местоположение:* ");
        if (location == null) {
            sb.append("любое");
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
        sb.append("\n\uD83C\uDF93 *Минимальный опыт:* ");
        if (experience == null) {
            sb.append("_отсутствует_");
        } else {
            sb.append(experience).append(" лет");
        }
    }

    private void appendMinSalary(StringBuilder sb, Integer minSalary) {
        sb.append("\n\uD83D\uDCB0 *Зарплата от:* ");
        if (minSalary != null) {
            sb.append(minSalary).append(" руб.");
        } else {
            sb.append("_не указана_");
        }
    }

    private void appendMaxSalary(StringBuilder sb, Integer maxSalary) {
        sb.append("\n\uD83D\uDCB5 *Зарплата до:* ");
        if (maxSalary != null) {
            sb.append(maxSalary).append(" руб.\n");
        } else {
            sb.append("_не указана_\n");
        }
    }
}