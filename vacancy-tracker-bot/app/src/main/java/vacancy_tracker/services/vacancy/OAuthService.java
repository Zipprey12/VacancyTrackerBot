package vacancy_tracker.services.vacancy;

public interface OAuthService {
    void exchangeCodeForToken(String code);

    void refreshAccessToken();
}
