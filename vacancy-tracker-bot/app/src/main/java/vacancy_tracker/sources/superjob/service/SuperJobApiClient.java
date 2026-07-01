package vacancy_tracker.sources.superjob.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.reactive.function.client.WebClient;

public abstract class SuperJobApiClient {

    @Getter
    private final WebClient webClient;

    protected SuperJobApiClient(@Qualifier("superJobWebClient") WebClient webClient) {
        this.webClient = webClient;
    }
}
