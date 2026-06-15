package vacancy_tracker.sources.superjob.service.company;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import vacancy_tracker.sources.superjob.model.response.SuperJobCompanyResponse;
import vacancy_tracker.sources.superjob.service.SuperJobApiClient;

@Slf4j
@Component
public class SuperJobCompaniesApiClient extends SuperJobApiClient {

    @Value("${superjob.api.companiesUrl}")
    private String companiesUrl;

    protected SuperJobCompaniesApiClient(WebClient superJobWebClient) {
        super(superJobWebClient);
    }

    public Mono<SuperJobCompanyResponse> getCompanyById(long companyId) {
        return getWebClient()
                .get()
                .uri(companiesUrl + "/" + companyId + "/")
                .retrieve()
                .bodyToMono(SuperJobCompanyResponse.class)
                .doOnError(WebClientResponseException.NotFound.class, e ->
                        log.warn("Компания c id {} не была найдена", companyId))
                .doOnError(e -> !(e instanceof WebClientResponseException.NotFound),
                        e -> log.error("Ошибка при запросе компании id={}: {}", companyId, e.getMessage()))
                .onErrorComplete();
    }
}
