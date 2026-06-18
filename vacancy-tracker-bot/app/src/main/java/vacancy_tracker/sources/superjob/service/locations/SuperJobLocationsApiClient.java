package vacancy_tracker.sources.superjob.service.locations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import vacancy_tracker.sources.superjob.model.dto.SuperJobRegionDto;
import vacancy_tracker.sources.superjob.model.dto.SuperJobTownDto;
import vacancy_tracker.sources.superjob.model.response.SuperJobCitiesResponse;
import vacancy_tracker.sources.superjob.model.response.SuperJobRegionsResponse;
import vacancy_tracker.sources.superjob.service.SuperJobApiClient;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
public class SuperJobLocationsApiClient extends SuperJobApiClient {

    public static final int RUSSIA_INDEX = 1;

    @Value("${superjob.api.regions-url}")
    private String regionsUrl;

    @Value("${superjob.api.cities-url}")
    private String townsUrl;

    protected SuperJobLocationsApiClient(WebClient superJobWebClient) {
        super(superJobWebClient);
    }

    public Mono<List<SuperJobRegionDto>> findAllRegionsWithoutCities() {
        var url = UriComponentsBuilder.fromHttpUrl(regionsUrl)
                .queryParam("all", true)
                .toUriString();

        return getWebClient()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(SuperJobRegionsResponse.class)
                .map(response -> {
                    log.info("SuperJob: Получено {} регионов", response.getTotal());
                    return response.getRegions()
                            .stream()
                            .filter(r -> r.getCountryId() == RUSSIA_INDEX)
                            .sorted(Comparator.comparing(SuperJobRegionDto::getName))
                            .toList();
                })
                .doOnError(WebClientResponseException.class,
                        e -> log.error("SuperJob: ошибка получения регионов, статус {}",
                                e.getStatusCode()))
                .doOnError(e -> !(e instanceof WebClientResponseException),
                        e -> log.error("SuperJob: ошибка при получении регионов", e))
                .onErrorReturn(List.of());
    }

    public Mono<List<SuperJobTownDto>> getAllTowns() {
        var url = buildTownsUrl();

        return getWebClient()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(SuperJobCitiesResponse.class)
                .retry(3)
                .map(response -> {
                    log.info("SuperJob: Получено {} городов", response.getTotal());
                    return response.getCities();
                })
                .doOnError(WebClientResponseException.class,
                        e -> log.error("SuperJob: ошибка получения городов, статус {}",
                                e.getStatusCode()))
                .doOnError(e -> !(e instanceof WebClientResponseException),
                        e -> log.error("SuperJob: ошибка при получении городов", e))
                .onErrorReturn(List.of());
    }

    private String buildTownsUrl() {
        return UriComponentsBuilder.fromHttpUrl(townsUrl)
                .queryParam("all", true)
                .queryParam("id_country", RUSSIA_INDEX)
                .toUriString();
    }
}
