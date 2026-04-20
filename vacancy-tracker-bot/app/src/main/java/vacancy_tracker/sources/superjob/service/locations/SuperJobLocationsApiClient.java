package vacancy_tracker.sources.superjob.service.locations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import vacancy_tracker.model.vacancy.entity.City;
import vacancy_tracker.model.vacancy.entity.Region;
import vacancy_tracker.sources.superjob.model.SuperJobCitiesResponse;
import vacancy_tracker.sources.superjob.model.SuperJobRegionsResponse;
import vacancy_tracker.sources.superjob.service.SuperJobApiClient;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SuperJobLocationsApiClient extends SuperJobApiClient {

    public static final int RUSSIA_INDEX = 1;

    private final RestTemplate restTemplate;

    @Value("${superjob.api.regionsUrl}")
    private String regionsUrl;

    @Value("${superjob.api.citiesUrl}")
    private String citiesUrl;

    public List<Region> findAllRegionsWithoutCities() {
        List<Region> regions = new LinkedList<>();

        try {
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<SuperJobRegionsResponse> response = restTemplate.exchange(
                    buildRegionsUrl(), HttpMethod.GET, entity, SuperJobRegionsResponse.class);

            var regionResponse = response.getBody();
            if (response.getStatusCode().is2xxSuccessful() && regionResponse != null) {
                log.info("SuperJob: Получено {} регионов", regionResponse.getTotal());
                regionResponse.getRegions().forEach(r -> {
                    if (r.getCountryId() == RUSSIA_INDEX) {
                        var region = Region.builder()
                                .id(r.getId())
                                .name(r.getName())
                                .build();
                        regions.add(region);
                    }
                });
            }

        } catch (Exception e) {
            log.error("Ошибка при попытке получения регионов: ", e);
        }

        return regions;
    }

    public List<City> getAllCities() {
        List<City> cities = new LinkedList<>();

        try {
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<SuperJobCitiesResponse> response = restTemplate.exchange(
                    buildCitiesUrl(), HttpMethod.GET, entity, SuperJobCitiesResponse.class);

            var citiesResponse = response.getBody();
            if (response.getStatusCode().is2xxSuccessful() && citiesResponse != null) {
                log.info("SuperJob: Получено {} городов", citiesResponse.getTotal());
                citiesResponse.getCities().forEach(c -> {
                    var city = City.builder()
                            .id(c.getId())
                            .name(c.getName())
                            .regionId(c.getRegionId())
                            .build();
                    cities.add(city);
                });
            }

        } catch (Exception e) {
            log.error("Ошибка при попытке получения регионов: ", e);
        }

        return cities;
    }

    private String buildRegionsUrl() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(regionsUrl);
        builder.queryParam("all", true);
        return builder.toUriString();
    }

    private String buildCitiesUrl() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(citiesUrl);
        builder.queryParam("all", true);
        builder.queryParam("id_country", RUSSIA_INDEX);
        return builder.toUriString();
    }
}
