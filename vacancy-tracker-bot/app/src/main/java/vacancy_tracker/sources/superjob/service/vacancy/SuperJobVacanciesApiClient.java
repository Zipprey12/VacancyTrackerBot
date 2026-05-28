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
import vacancy_tracker.model.api.Location;
import vacancy_tracker.model.api.dto.VacancySearchFilter;
import vacancy_tracker.services.DateUtil;
import vacancy_tracker.sources.superjob.model.response.SuperJobVacanciesResponse;
import vacancy_tracker.sources.superjob.service.SuperJobApiClient;
import vacancy_tracker.sources.superjob.service.locations.SuperJobRegionsConnectingService;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class SuperJobVacanciesApiClient extends SuperJobApiClient {

    private final RestTemplate restTemplate;
    private final SuperJobRegionsConnectingService connectingService;

    @Value("${superjob.api.vacanciesUrl}")
    private String vacanciesUrl;

    public Optional<SuperJobVacanciesResponse> searchVacancies(VacancySearchFilter filter, int limit, int page) {

        URI uri = buildUrl(filter, limit, page);
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
                var body = response.getBody();
                log.info("SuperJob: получено {} вакансий из {}", body.getVacanciesSafe().size(), body.getTotal());
                var result = response.getBody();
                result.setOffset(page * limit);
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

    private URI buildUrl(VacancySearchFilter filter, int limit, int page) {
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

        limit = Math.clamp(limit, 0, 100);
        builder.queryParam("count", limit);

        var now = DateUtil.toUnixSeconds(LocalDateTime.now());
        builder.queryParam("date_published_to", now);

        if (filter.getMinSalary() != null || filter.getMaxSalary() != null) {
            builder.queryParam("no_agreement", 1);
        }

        var modifiedFrom = filter.getModifiedFrom();
        if (modifiedFrom != null) {
            var unix = modifiedFrom.atZone(ZoneId.systemDefault()).toEpochSecond();
            builder.queryParam("date_published_from", unix);
        }

        builder.queryParam("order_field", "date");
        builder.queryParam("sort_new", 1);
        builder.queryParam("order_direction", "desc");
        builder.queryParam("page", page);

        StringBuilder url = new StringBuilder(builder.toUriString());
        var text = filter.getText();
        if (text != null && !text.isBlank()) {
            addText(url, text);
        }

        return URI.create(url.toString());
    }

    private static void addText(StringBuilder url, String text) {
        String encoded = URLEncoder.encode(text, StandardCharsets.UTF_8);
        url.append("&keywords[0][srws]=1")
                .append("&keywords[0][skwc]=particular")
                .append("&keywords[0][keys]=").append(encoded);
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

    private void addLocation(UriComponentsBuilder builder, Location location) {
        var town = location.getTown();
        if (town != null) {
            builder.queryParam("town", town.getId());
            return;
        }

        var region = location.getRegion();
        if (region != null) {
            var code = location.getRegion().getCode();
            var id = connectingService.getIdByCode(code);
            if (id.isEmpty()) {
                log.error("Во время поиска произошла ошибка преобразования кода региона в id {}:", code);
                return;
            }
            builder.queryParam("o", id);
        }
    }
}
