package vacancy_tracker.sources.superjob.service.vacancy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import vacancy_tracker.model.domain.Location;
import vacancy_tracker.model.search.VacancySearchFilter;
import vacancy_tracker.services.util.DateUtil;
import vacancy_tracker.sources.superjob.model.response.SuperJobVacanciesResponse;
import vacancy_tracker.sources.superjob.service.SuperJobApiClient;
import vacancy_tracker.sources.superjob.service.locations.SuperJobRegionsConnectingService;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@Slf4j
public class SuperJobVacanciesApiClient extends SuperJobApiClient {

    private final SuperJobRegionsConnectingService connectingService;

    @Value("${superjob.api.vacancies-url}")
    private String vacanciesUrl;

    protected SuperJobVacanciesApiClient(WebClient superJobWebClient,
                                         SuperJobRegionsConnectingService connectingService) {
        super(superJobWebClient);
        this.connectingService = connectingService;
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

    public Mono<SuperJobVacanciesResponse> searchVacancies(VacancySearchFilter filter, int limit, int page) {
        var uri = buildUrl(filter, limit, page);
        log.info("SuperJob: запрос на получение вакансий {}", uri);

        return getWebClient()
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(SuperJobVacanciesResponse.class)
                .doOnSuccess(r -> {
                            log.debug("SuperJob: получено {} вакансий из {}",
                                    r.getVacanciesSafe().size(), r.getTotal());
                            r.setOffset(page * limit);
                        }
                )
                .doOnError(WebClientResponseException.class,
                        e -> log.warn("SuperJob: статус {}", e.getStatusCode()))
                .doOnError(e -> !(e instanceof WebClientResponseException), e ->
                        log.error("SuperJob: ошибка запроса", e))
                .onErrorComplete();
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
