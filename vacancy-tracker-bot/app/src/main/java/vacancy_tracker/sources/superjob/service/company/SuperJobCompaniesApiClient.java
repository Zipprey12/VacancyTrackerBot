package vacancy_tracker.sources.superjob.service.company;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import vacancy_tracker.sources.superjob.model.response.SuperJobCompanyResponse;
import vacancy_tracker.sources.superjob.service.SuperJobApiClient;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SuperJobCompaniesApiClient extends SuperJobApiClient {

    private final RestTemplate restTemplate;

    @Value("${superjob.api.companiesUrl}")
    private String companiesUrl;

    public Optional<SuperJobCompanyResponse> getCompanyById(long companyId) {
        String url = companiesUrl + "/" + companyId + "/";

        try {
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<SuperJobCompanyResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, SuperJobCompanyResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Optional.of(response.getBody());
            }
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Компания c id {} не была найдена", companyId, e);
        } catch (Exception e) {
            log.error("Ошибка при запросе компании id={}: {}", companyId, e.getMessage());
        }
        return Optional.empty();
    }
}
