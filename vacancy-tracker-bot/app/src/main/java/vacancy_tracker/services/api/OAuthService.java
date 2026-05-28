package vacancy_tracker.services.api;

public interface OAuthService {
    void exchangeCodeForToken(String code);

    void refreshAccessToken();
}
