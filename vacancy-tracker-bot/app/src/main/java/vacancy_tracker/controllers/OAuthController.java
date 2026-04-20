package vacancy_tracker.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vacancy_tracker.services.vacancy.OAuthService;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/callback")
@RequiredArgsConstructor
public class OAuthController {

    private final Map<String, OAuthService> OAuthServices;

    @GetMapping("/{source}")
    public boolean handleRedirect(@PathVariable String source,
                                 @RequestParam("code") String code){
        var service = OAuthServices.get(source);
        if(source == null){
            log.error("Ошибка: неизвестный источник авторизации: {}", source);
            return false;
        }

        service.exchangeCodeForToken(code);
        log.info("Авторизация через {} прошла успешно", source);
        return true;
    }
}
