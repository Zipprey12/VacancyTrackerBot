package vacancy_tracker.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;


@Configuration
public class WebClientConfig {

    private static final int READ_TIMEOUT_S = 20;
    private static final int CONNECTION_TIMEOUT_MS = 5_000;

    @Value("${superjob.secret-key}")
    private String superJobApiKey;

    @Value("${trudvsem.api.base-url}")
    private String trudVsemBaseUrl;

    @Bean
    public WebClient superJobWebClient() {
        var provider = ConnectionProvider.builder("superjob-pool")
                .maxConnections(10)
                .maxIdleTime(Duration.ofSeconds(10))
                .maxLifeTime(Duration.ofSeconds(60))
                .evictInBackground(Duration.ofSeconds(30))
                .build();

        return WebClient.builder()
                .defaultHeader("X-Api-App-Id", superJobApiKey)
                .clientConnector(new ReactorClientHttpConnector(buildHttpClient(provider)))
                .codecs(c -> c.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build();
    }

    @Bean
    public WebClient trudVsemWebClient() {
        return WebClient.builder()
                .baseUrl(trudVsemBaseUrl)
                .defaultHeader("Accept",
                        "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .clientConnector(new ReactorClientHttpConnector(buildHttpClient(null)))
                .build();
    }

    private HttpClient buildHttpClient(ConnectionProvider provider) {
        var client = provider != null
                ? HttpClient.create(provider)
                : HttpClient.create();

        return client
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECTION_TIMEOUT_MS)
                .responseTimeout(Duration.ofSeconds(READ_TIMEOUT_S))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(READ_TIMEOUT_S, TimeUnit.SECONDS))
                )
                .followRedirect(true);
    }
}