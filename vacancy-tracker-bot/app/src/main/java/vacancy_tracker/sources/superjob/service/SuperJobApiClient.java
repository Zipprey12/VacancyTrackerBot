package vacancy_tracker.sources.superjob.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class SuperJobApiClient {

    @Value("${superjob.secretKey}")
    private String appKey;

    protected HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Api-App-Id", appKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
