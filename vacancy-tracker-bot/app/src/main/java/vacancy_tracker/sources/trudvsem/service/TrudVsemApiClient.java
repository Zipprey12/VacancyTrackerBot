package vacancy_tracker.sources.trudvsem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import vacancy_tracker.model.search.VacancySearchFilter;
import vacancy_tracker.sources.trudvsem.model.TrudVsemResponse;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrudVsemApiClient {

    public static final int COUNT_LIMIT = 10;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private final WebClient trudVsemWebClient;

    @Value("${trudvsem.api.base-url}")
    private String baseUrl;

    public Mono<TrudVsemResponse> searchVacancies(VacancySearchFilter filter, int limit, int offset) {
        var url = buildUrl(filter, limit, offset);
        log.info("Requesting vacancies from: {}", url);

        return trudVsemWebClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(TrudVsemResponse.class)
                .doOnSuccess(r -> {
                    var vacancies = r.getVacanciesSafe();
                    log.debug("TrudVsem: Получено {} вакансий из {}",
                            vacancies != null ? vacancies.size() : 0,
                            r.getMeta().getTotal());
                })
                .doOnError(WebClientResponseException.class, e ->
                        log.warn("TrudVsem: получен код {}", e.getStatusCode())
                )
                .doOnError(e -> !(e instanceof WebClientResponseException),
                        e -> log.error("TrudVsem: ошибка при получении вакансий", e))
                .onErrorComplete();
    }

    private String buildUrl(VacancySearchFilter filter, int limit, int offset) {
        StringBuilder url = new StringBuilder(baseUrl + "/vacancies");
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
