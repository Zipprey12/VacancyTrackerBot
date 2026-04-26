package vacancy_tracker.sources.trudvsem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import vacancy_tracker.model.api.dto.VacancySearchFilter;
import vacancy_tracker.sources.trudvsem.model.TrudVsemResponse;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrudVsemApiClient {

    private static final String BASE_URL = "http://opendata.trudvsem.ru/api/v1/vacancies";
    public static final int COUNT_LIMIT = 10;

    private final RestTemplate restTemplate;

    public Optional<TrudVsemResponse> searchVacancies(VacancySearchFilter filter) {
        try {
            String url = buildUrl(filter);
            log.info("Requesting vacancies from: {}", url);

            ResponseEntity<TrudVsemResponse> response = restTemplate.getForEntity(
                    url,
                    TrudVsemResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                var vacancies = response.getBody().getVacancies();
                log.info("Found {} vacancies", vacancies != null ? vacancies.size() : 0);
                return Optional.of(response.getBody());
            } else {
                log.warn("Received non-OK response: {}", response.getStatusCode());
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Failed to fetch vacancies from trudvsem.ru", e);
            return Optional.empty();
        }
    }


    public Optional<TrudVsemResponse> searchByKeyword(String keyword, int limit) {
        VacancySearchFilter filter = VacancySearchFilter.builder()
                .text(keyword)
                .limit(limit)
                .build();
        return searchVacancies(filter);
    }

    public boolean ping(){
        try {
            ResponseEntity<String> response = restTemplate.getForEntity("http://opendata.trudvsem.ru/api", String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (ResourceAccessException e) {
            return false;
        } catch (RestClientException e) {
            return false;
        }
    }

    private String buildUrl(VacancySearchFilter filter) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL);

        if (filter.getText() != null) {
            builder.queryParam("text", filter.getText());
        }
        if (filter.getLocation() != null) {
            builder.queryParam("regionCode", filter.getLocation());
        }
        if (filter.getOffset() != null) {
            builder.queryParam("offset", filter.getOffset());
        }
        if (filter.getLimit() != null) {
            builder.queryParam("limit", Math.min(filter.getLimit(), COUNT_LIMIT));
        }
        if (filter.getModifiedFrom() != null) {
            builder.queryParam("modifiedFrom", filter.getModifiedFrom());
        }
        if (filter.getMinSalary() != null) {
            builder.queryParam("salaryMin", filter.getMinSalary());
        }

        return builder.toUriString();
    }
}
