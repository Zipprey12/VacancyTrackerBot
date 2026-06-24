package vacancy_tracker.services.telegram.callback.handlers.vacancies;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.domain.VacanciesSource;
import vacancy_tracker.model.search.VacanciesSearchRequest;
import vacancy_tracker.model.telegram.callback.CallbackArgs;
import vacancy_tracker.model.telegram.callback.CallbackData;
import vacancy_tracker.model.telegram.callback.VacanciesCallbackKeys;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.SearchActionParams;
import vacancy_tracker.model.telegram.session.PublishType;
import vacancy_tracker.model.telegram.settings.VacanciesShownParams;
import vacancy_tracker.services.telegram.actions.vacancies.SendVacanciesAction;
import vacancy_tracker.services.telegram.callback.handlers.NavigationCallbackHandler;
import vacancy_tracker.services.util.DateUtil;
import vacancy_tracker.services.util.StringUtil;

import java.util.Optional;

import static vacancy_tracker.model.telegram.callback.VacancySearchArg.*;

@Slf4j
@Component
public class ShowVacanciesCallbackHandler extends NavigationCallbackHandler<SearchActionParams> {

    private final SendVacanciesAction action;

    protected ShowVacanciesCallbackHandler(SendVacanciesAction handler) {
        super(VacanciesCallbackKeys.GET_VACANCIES.getKey(), handler);
        action = handler;
    }

    @Override
    protected Optional<SearchActionParams> tryCastSelectedValue(String value) {
        try {
            var source = VacanciesSource.valueOf(value);
            var searchParams = VacanciesSearchRequest.builder()
                    .page(0)
                    .source(source)
                    .build();
            var shownParams = new VacanciesShownParams();
            return Optional.of(new SearchActionParams(searchParams, shownParams));

        } catch (Exception e) {
            log.error("В callback указан несуществующий источник: {}", value);
            return Optional.empty();
        }
    }

    @Override
    protected void executeWithEmptyKey(MessageData message, CallbackData data) {
        var args = data.args();
        if (args != null && !args.isEmpty()) {
            var time = args.getByKey(FROM.getKey()).getValue();
            log.info(time);
            var casted = StringUtil.parseLong(time);
            if (casted.isPresent()) {
                var params = new VacanciesSearchRequest();
                params.setStartDate(DateUtil.fromUnixSeconds(casted.get()));

                var p = new SearchActionParams(params, null);
                action.handleWithParameterAsync(message, p);
                return;
            }
        }
        action.executeAsync(message)
                .thenAccept(v -> finish(message.getCallbackId()));
    }

    @Override
    protected void navigate(MessageData message, CallbackData data) {
        var args = data.args();
        var params = extractParams(args);
        if (params == null) {
            getHandler().execute(message);
            return;
        }
        params.getSearchParams().setPage(data.targetPage());
        message.setSource(PublishType.UPDATE);
        action.handleWithParameterAsync(message, params);
    }

    @Override
    public void handleCastedData(SearchActionParams data, MessageData messageData) {
        action.handleWithParameterAsync(messageData, data);
    }

    private SearchActionParams extractParams(CallbackArgs args) {
        try {
            var searchParams = new VacanciesSearchRequest();
            var shownParams = new VacanciesShownParams();
            var result = new SearchActionParams(searchParams, shownParams);

            var source = VacanciesSource.valueOf(args.getByKey(SOURCE.getKey()).getValue());
            searchParams.setSource(source);

            var dateValue = args.getByKey(FROM.getKey());
            if (dateValue != null) {
                var dateTime = DateUtil.fromUnixSeconds(Long.parseLong(dateValue.getValue()));
                searchParams.setStartDate(dateTime);
            }

            var hasAnotherValue = args.getByKey(HAS_ANOTHER.getKey());
            if (hasAnotherValue != null) {
                var casted = StringUtil.parseBoolean(hasAnotherValue.getValue());
                casted.ifPresent(shownParams::setHasAnother);
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }
}
