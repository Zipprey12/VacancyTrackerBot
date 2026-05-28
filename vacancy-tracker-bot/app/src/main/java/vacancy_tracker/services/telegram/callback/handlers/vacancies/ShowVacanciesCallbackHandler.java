package vacancy_tracker.services.telegram.callback.handlers.vacancies;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.api.VacanciesSource;
import vacancy_tracker.model.telegram.CallingSource;
import vacancy_tracker.model.telegram.callback.CallbackArgs;
import vacancy_tracker.model.telegram.callback.CallbackData;
import vacancy_tracker.model.telegram.callback.VacanciesCallbackKeys;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.VacanciesSearchParams;
import vacancy_tracker.services.DateUtil;
import vacancy_tracker.services.StringUtil;
import vacancy_tracker.services.telegram.callback.handlers.NavigationCallbackHandler;
import vacancy_tracker.services.telegram.command.vacancies.SendAllVacanciesCommand;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
public class ShowVacanciesCallbackHandler extends NavigationCallbackHandler<VacanciesSearchParams> {

    protected ShowVacanciesCallbackHandler(SendAllVacanciesCommand handler) {
        super(VacanciesCallbackKeys.GET_VACANCIES.getKey(), handler);
    }

    @Override
    protected Optional<VacanciesSearchParams> tryCastSelectedValue(String value) {
        try {
            var source = VacanciesSource.valueOf(value);
            var params = VacanciesSearchParams.builder()
                    .page(0)
                    .source(source)
                    .build();
            return Optional.of(params);

        } catch (Exception e) {
            log.error("В callback указан несуществующий источник: {}", value);
            return Optional.empty();
        }
    }

    @Override
    protected void executeWithEmptyKey(MessageData messageData, CallbackData data) {
        var args = data.args();
        if (args != null && !args.isEmpty()) {
            var time = args.atIndex(0).getValue();
            log.info(time);
            var casted = StringUtil.parseLong(time);
            if (casted.isPresent()) {
                var params = new VacanciesSearchParams();
                params.setStartDate(DateUtil.fromUnixSeconds(casted.get()));
                getHandler().handleWithParameter(messageData, params);
                return;
            }
        }
        super.executeWithEmptyKey(messageData, data);
    }

    @Override
    protected void navigate(MessageData message, CallbackData data) {
        var args = data.args();
        var params = extractParams(args);
        if (params == null) {
            getHandler().execute(message);
            return;
        }
        params.setPage(data.targetPage());
        message.setSource(CallingSource.CALLBACK);
        getHandler().handleWithParameter(message, params);
    }

    private VacanciesSearchParams extractParams(CallbackArgs args) {
        try {
            var source = VacanciesSource.valueOf(args.atIndex(0).getValue());
            LocalDateTime dateTime = null;
            if (args.size() > 1) {
                dateTime = DateUtil.fromUnixSeconds(Long.parseLong(args.atIndex(1).getValue()));
            }
            var params = new VacanciesSearchParams();
            params.setSource(source);
            params.setStartDate(dateTime);
            return params;
        } catch (Exception e) {
            return null;
        }
    }
}
