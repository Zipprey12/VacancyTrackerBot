package vacancy_tracker.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BotConfig {

    @Value("${bot.token}")
    private String botToken;

    @Bean
    public TelegramClient telegramClient() {
        return new OkHttpTelegramClient(botToken);
    }

    @Bean
    public Map<Integer, String> additionalRegionNames() {
        return Map.of(
                71, "Тюменская область, включая Ханты-Мансийский АО и Ямало-Ненецкий АО"
        );
    }
}
