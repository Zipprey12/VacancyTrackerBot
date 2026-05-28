package vacancy_tracker.sources.trudvsem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import vacancy_tracker.model.api.dto.VacancySearchFilter;
import vacancy_tracker.sources.trudvsem.model.TrudVsemResponse;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrudVsemApiClient {

    public static final int COUNT_LIMIT = 10;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private static final String BASE_URL = "http://opendata.trudvsem.ru/api/v1/vacancies";

    private final RestTemplate restTemplate;

    public Optional<TrudVsemResponse> searchVacancies(VacancySearchFilter filter, int limit, int offset) {
        try {
            String url = buildUrl(filter, limit, offset);
            log.info("Requesting vacancies from: {}", url);

            var headers = new HttpHeaders();
            headers.set("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            var entity = new HttpEntity<>(headers);

            ResponseEntity<TrudVsemResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, TrudVsemResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                var body = response.getBody();
                var vacancies = body.getVacanciesSafe();
                log.info("TrudVsem: Получено {} вакансий из {}", vacancies != null ?
                        vacancies.size() : 0, body.getMeta().getTotal());
                return Optional.of(response.getBody());

            } else {
                log.warn("TrudVsem: получен код {}", response.getStatusCode());
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("TrudVsem: ошибка при получении вакансий", e);
            return Optional.empty();
        }
    }

    private String buildUrl(VacancySearchFilter filter, int limit, int offset) {
        StringBuilder url = new StringBuilder(BASE_URL);

        if (filter.getLocation() != null && filter.getLocation().getRegion() != null) {
            url.append("/region/")
                    .append(buildRegionString(filter.getLocation().getRegion().getCode()));
        }

        url.append("?offset=").append(offset)
                .append("&limit=").append(Math.clamp(limit, 0, COUNT_LIMIT));

        if (filter.getModifiedFrom() != null) {
            url.append("&modifiedFrom=")
                    .append(getDateTimeString(filter.getModifiedFrom()));
        }
        if (filter.getText() != null && !filter.getText().isBlank()) {
            url.append("&text=").append(filter.getText());
        }
        if (filter.getExperience() != null) {
            url.append("&experienceTo=").append(filter.getExperience().intValue());
        }
        if (filter.getMinSalary() != null) {
            url.append("&salaryMin=").append(filter.getMinSalary());
        }
        return url.toString();
    }

    private String getDateTimeString(LocalDateTime dateTime) {
        return dateTime.atOffset(ZoneOffset.UTC).format(FORMATTER);
    }

    private String buildRegionString(int regionCode) {
        return String.format("%02d", regionCode) + "00000000000";
    }
}
