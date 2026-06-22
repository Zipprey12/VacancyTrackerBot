package vacancy_tracker.services.telegram.actions.vacancies;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.domain.RequestType;
import vacancy_tracker.model.search.SearchResult;
import vacancy_tracker.model.search.VacanciesSearchData;
import vacancy_tracker.model.search.VacanciesSearchParams;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.model.telegram.dto.SearchActionParams;
import vacancy_tracker.model.telegram.execution.ExecutionResult;
import vacancy_tracker.model.telegram.session.PublishType;
import vacancy_tracker.model.telegram.settings.VacanciesShownParams;
import vacancy_tracker.services.api.VacanciesSearcher;
import vacancy_tracker.services.telegram.actions.AsyncAction;
import vacancy_tracker.services.telegram.actions.message.VacanciesResultMessage;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.settings.SearchFiltersService;

import java.util.concurrent.CompletableFuture;

import static vacancy_tracker.model.telegram.execution.ExecutionFailReason.EMPTY_RESULT;
import static vacancy_tracker.model.telegram.execution.ExecutionFailReason.EXCEPTION;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendVacanciesAction extends AsyncAction<SearchActionParams> {

    public static final int VACANCIES_COUNT_LIMIT = 10;
    private final VacanciesSearcher vacanciesSearcher;
    private final SearchFiltersService settingsService;
    private final VacanciesResultMessage resultMessage;
    private final SendingAndUpdatingMessagePublisher publisher;

    @Override
    public CompletableFuture<Void> executeAsync(MessageData messageData) {
        return handleWithParameterAsync(messageData, new SearchActionParams(
                new VacanciesSearchParams(0, null, null, RequestType.MANUAL),
                new VacanciesShownParams(true, true)
        ));
    }

    @Override
    public CompletableFuture<ExecutionResult> executeWithCompletionCheck(MessageData messageData, SearchActionParams parameter) {
        return tryShow(messageData, parameter)
                .thenApply(value ->
                        Boolean.TRUE.equals(value) ? ExecutionResult.success() : ExecutionResult.fail(EMPTY_RESULT))
                .exceptionally(e -> {
                    log.error(ERROR_MESSAGE, e);
                    return ExecutionResult.fail(EXCEPTION);
                });
    }

    @Override
    public CompletableFuture<Void> handleWithParameterAsync(MessageData messageData, SearchActionParams parameters) {
        return tryShow(messageData, parameters)
                .thenApply(result -> null);
    }

    public CompletableFuture<Boolean> tryShow(MessageData messageData, SearchActionParams parameters) {
        var searchParams = parameters.getSearchParams();
        var shownParams = parameters.getShownParams();
        var filter = settingsService.get(messageData.getChatId());

        if (messageData.getSendTime().isBefore(filter.getUpdatedAt())) {
            searchParams.setPage(0);
            messageData.setSource(PublishType.SEND);
        }

        filter.setRequestType(searchParams.getRequestType());
        if (searchParams.getStartDate() != null) {
            filter.setModifiedFrom(searchParams.getStartDate());
        }

        var messageId = messageData.getSource() == PublishType.UPDATE ?
                messageData.getMessageId() : null;

        var source = searchParams.getSource();
        var page = searchParams.getPage();
        var params = VacanciesSearchData.builder()
                .filter(filter)
                .limit(VACANCIES_COUNT_LIMIT)
                .chatId(messageData.getChatId())
                .page(page)
                .messageId(messageId)
                .build();

        return vacanciesSearcher.searchWithOutcome(params, source)
                .thenApply(outcome -> {
                    var result = outcome.result();
                    if (result.getNotEmptyResponseCount() == 0 && !shownParams.isShowIfEmpty()) {
                        return false;
                    }
                    var message = new OutgoingMessage(messageData);
                    handleResult(parameters, result, message);

                    var realMessageId = publisher.publish(message);
                    outcome.onPublished().accept(realMessageId, page + 1);

                    return true;
                });
    }

    private void handleResult(SearchActionParams parameters, SearchResult result, OutgoingMessage message) {
        var source = parameters.getSearchParams().getSource();
        var shownParams = parameters.getShownParams();

        log.debug("non empty: {}", result.getNotEmptySources());
        if (result.getNotEmptyResponseCount() == 1) {
            var response = result.getNotEmptyResponses().getFirst();
            if (source == null) {
                var params = new VacanciesShownParams(true, false);
                resultMessage.fillMessage(response, message, params);
            } else {
                resultMessage.fillMessage(response, message, shownParams);
            }
        } else {
            resultMessage.fillMessage(result, message);
        }
    }
}
