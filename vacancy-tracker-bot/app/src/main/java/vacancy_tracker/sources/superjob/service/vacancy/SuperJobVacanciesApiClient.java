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
import vacancy_tracker.model.api.entity.Location;
import vacancy_tracker.sources.superjob.model.response.SuperJobVacanciesResponse;
import vacancy_tracker.sources.superjob.service.SuperJobApiClient;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class SuperJobVacanciesApiClient extends SuperJobApiClient {

    private final RestTemplate restTemplate;

    @Value("${superjob.api.vacanciesUrl}")
    private String vacanciesUrl;

    public Optional<SuperJobVacanciesResponse> searchVacancies(VacancySearchFilter filter) {

        URI uri = buildUrl(filter);
        log.info("SuperJob: запрос на получение вакансий {}", uri);

        try {
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<SuperJobVacanciesResponse> response = restTemplate.exchange(
                    uri,
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

    private URI buildUrl(VacancySearchFilter filter) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(vacanciesUrl);

        if (filter.getLocation() != null) {
            addLocation(builder, filter.getLocation());
        }
        if (filter.getMinSalary() != null) {
            builder.queryParam("payment_from", filter.getMinSalary());
        }
        if (filter.getMaxSalary() != null) {
            builder.queryParam("payment_to", filter.getMaxSalary());
        }
        if (filter.getExperience() != null) {
            addExperience(builder, filter.getExperience());
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

        if (filter.getMinSalary() != null || filter.getMaxSalary() != null) {
            builder.queryParam("no_agreement", 1);
        }

        builder.queryParam("order_field", "date");
        builder.queryParam("order_direction", "desc");

        StringBuilder url = new StringBuilder(builder.toUriString());
        if (filter.getText() != null) {
            addText(url, filter.getText());
        }

        return URI.create(url.toString());
    }

    private static void addText(StringBuilder url, String text) {
        String encoded = URLEncoder.encode(text, StandardCharsets.UTF_8);
        url.append("&keywords[0][srws]=1")
                .append("&keywords[0][skwc]=particular")
                .append("&keywords[0][keys]=").append(encoded);
    }

    private static void addLocation(UriComponentsBuilder builder, Location location) {
        var town = location.getTown();
        if (town != null) {
            builder.queryParam("town", town.getId());
            return;
        }

        var region = location.getRegion();
        if (region != null) {
            builder.queryParam("o", location.getRegion().getId());
        }
    }

    private static void addExperience(UriComponentsBuilder builder, float experience) {
        var value = 0;
        if (experience < 1) {
            return;
        }
        if (experience < 3) {
            value = 1;
        } else if (experience < 6) {
            value = 3;
        } else {
            value = 6;
        }
        builder.queryParam("experience", value);
    }
}
