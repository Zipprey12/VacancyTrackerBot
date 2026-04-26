package vacancy_tracker.sources.superjob.service.vacancy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import vacancy_tracker.model.api.dto.VacancySearchFilter;
import vacancy_tracker.sources.superjob.model.SuperJobVacanciesResponse;
import vacancy_tracker.sources.superjob.service.SuperJobApiClient;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class SuperJobVacanciesApiClient extends SuperJobApiClient {

    private final RestTemplate restTemplate;

    @Value("${superjob.api.vacanciesUrl}")
    private String vacanciesUrl;

    public Optional<SuperJobVacanciesResponse> searchVacancies(VacancySearchFilter filter) {
        String url = buildUrl(filter);
        log.debug("SuperJob: запрос на получение вакансий {}", url);

        try {
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<SuperJobVacanciesResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    SuperJobVacanciesResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("SuperJob: найдено {} вакансий", response.getBody().getVacanciesSafe().size());
                return Optional.of(response.getBody());
            } else {
                log.warn("SuperJob: статус {}", response.getStatusCode());
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("SuperJob: ошибка запроса", e);
            return Optional.empty();
        }
    }

    private String buildUrl(VacancySearchFilter filter) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(vacanciesUrl);

        if (filter.getText() != null) {
            builder.queryParam("keyword", filter.getText());
        }
        if (filter.getLocation() != null) {
            builder.queryParam("town", filter.getLocation());
        }
        if (filter.getMinSalary() != null) {
            builder.queryParam("payment_from", filter.getMinSalary());
        }
        if (filter.getMaxSalary() != null) {
            builder.queryParam("payment_to", filter.getMaxSalary());
        }
        if (filter.getLimit() != null) {
            builder.queryParam("count", Math.min(filter.getLimit(), 100));
        } else {
            builder.queryParam("count", 20);
        }
        if (filter.getOffset() != null) {
            int page = filter.getOffset() / filter.getLimit();
            builder.queryParam("page", page);
        }

        builder.queryParam("no_agreement", 1);  // Только с указанной зарплатой
        builder.queryParam("order_field", "date");
        builder.queryParam("order_direction", "desc");

        return builder.toUriString();
    }
}
