package vacancy_tracker.services.telegram.view.formatters.vacancies;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.domain.Location;
import vacancy_tracker.model.domain.VacanciesSource;
import vacancy_tracker.model.domain.Vacancy;
import vacancy_tracker.model.search.SearchResult;
import vacancy_tracker.services.telegram.view.utils.DatesFormatUtil;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class VacanciesMessageFormatter {

    public static final int MAX_VACANCIES = 10;
    public static final String RUB = " руб.";

    protected static void addHeader(StringBuilder sb, SearchResult result) {
        addHeader(sb, result.getModifiedFrom(), result.getTotalCount());
    }

    protected static void addHeader(StringBuilder sb, LocalDateTime modifiedFrom, long totalCount) {
        if (modifiedFrom != null) {
            addScheduledHeader(sb, totalCount, modifiedFrom);
        } else {
            addManualHeader(sb, totalCount);
        }
    }

    private static void addManualHeader(StringBuilder stringBuilder, long totalCount) {
        if (totalCount >= 0) {
            stringBuilder.append("Всего вакансий: *").append(totalCount).append("*\n\n");
        } else {
            stringBuilder.append("*Полученные вакансии:*\n\n");
        }
    }

    private static void addScheduledHeader(StringBuilder sb, long count, LocalDateTime from) {
        sb.append("Вакансии за период с *").append(DatesFormatUtil.formatDateTime(from));
        if (count >= 0) {
            sb.append("*\nВсего: ").append(count);
        }
        sb.append("\n\n");
    }

    protected void addVacancies(StringBuilder sb, List<Vacancy> vacancies, int maxCount) {
        int current = 0;
        for (var vacancy : vacancies) {
            if (current >= maxCount) {
                return;
            }
            fillMessage(vacancy, sb);
            sb.append("\n");
            current++;
        }
    }

    protected void addSource(StringBuilder sb, VacanciesSource source) {
        sb.append("Источник: *")
                .append(source.getName())
                .append("*\n\n");
    }

    private void fillMessage(Vacancy vacancy, StringBuilder sb) {
        sb.append("💼 [").append(vacancy.getName())
                .append("](")
                .append(vacancy.getVacancyUrl())
                .append(")\n");
        var company = vacancy.getCompany();
        if (company != null && company.getName() != null) {
            sb.append("🏢[ ").append(company.getName()).append("](").append(company.getUrl()).append(")\n");
        }
        sb.append("💰 ").append(getSalaryString(vacancy.getSalaryMin(), vacancy.getSalaryMax())).append("\n")
                .append("📍 ").append(getLocationString(vacancy.getLocation())).append("\n");
    }

    private String getSalaryString(int min, int max) {
        if (min == 0 && max == 0) {
            return "не указана";
        }
        if (min == 0 || min == max) {
            return max + RUB;
        }
        if (max == 0) {
            return min + RUB;
        }
        return min + " - " + max + RUB;
    }

    private String getLocationString(Location location) {
        if (location.getTown() != null) {
            return location.getTown().getName();
        }
        if (location.getRegion() != null) {
            return location.getRegion().getName();
        }
        return "Не указан";
    }
}
