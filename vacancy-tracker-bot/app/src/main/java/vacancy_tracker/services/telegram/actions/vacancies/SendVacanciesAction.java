package vacancy_tracker.services.telegram.actions.vacancies;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.domain.RequestType;
import vacancy_tracker.model.search.SearchResult;
import vacancy_tracker.model.search.VacanciesSearchParams;
import vacancy_tracker.model.search.VacanciesSearchRequest;
import vacancy_tracker.model.search.VacancySearchFilter;
import vacancy_tracker.model.telegram.dto.MessageData;
import vacancy_tracker.model.telegram.dto.OutgoingMessage;
import vacancy_tracker.model.telegram.dto.SearchActionParams;
import vacancy_tracker.model.telegram.execution.ExecutionResult;
import vacancy_tracker.model.telegram.session.PublishType;
import vacancy_tracker.model.telegram.settings.VacanciesShownParams;
import vacancy_tracker.services.api.SearchVacanciesService;
import vacancy_tracker.services.telegram.actions.AsyncAction;
import vacancy_tracker.services.telegram.actions.message.VacanciesResultMessage;
import vacancy_tracker.services.telegram.command.publishers.SendingAndUpdatingMessagePublisher;
import vacancy_tracker.services.telegram.settings.SearchFiltersService;
import vacancy_tracker.services.telegram.view.formatters.vacancies.VacanciesSearchMessageFormatter;

import java.util.concurrent.CompletableFuture;

import static vacancy_tracker.model.telegram.execution.ExecutionFailReason.EMPTY_RESULT;
import static vacancy_tracker.model.telegram.execution.ExecutionFailReason.EXCEPTION;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendVacanciesAction extends AsyncAction<SearchActionParams> {

    public static final int VACANCIES_COUNT_LIMIT = 10;
    private final SearchFiltersService settingsService;
    private final VacanciesResultMessage resultMessage;
    private final SendingAndUpdatingMessagePublisher publisher;
    private final SearchVacanciesService searchService;

    @Override
    public CompletableFuture<Void> executeAsync(MessageData messageData) {
        return handleWithParameterAsync(messageData, new SearchActionParams(
                new VacanciesSearchRequest(0, null, null, RequestType.MANUAL),
                new VacanciesShownParams(true, true)
        ));
    }

    @Override
    public CompletableFuture<ExecutionResult> executeWithCompletionCheck(MessageData messageData, SearchActionParams parameter) {
        var message = showSearchMessage(messageData);
        return tryShow(message, parameter)
                .thenApply(value ->
                        Boolean.TRUE.equals(value) ? ExecutionResult.success() : ExecutionResult.fail(EMPTY_RESULT))
                .exceptionally(e -> {
                    log.error(ERROR_MESSAGE, e);
                    return ExecutionResult.fail(EXCEPTION);
                });
    }

    @Override
    public CompletableFuture<Void> handleWithParameterAsync(MessageData messageData, SearchActionParams parameters) {
        var message = showSearchMessage(messageData);
        return tryShow(message, parameters)
                .thenApply(result -> null);
    }

    public CompletableFuture<Boolean> tryShow(OutgoingMessage message, SearchActionParams parameters) {
        var searchRequest = parameters.getSearchParams();
        var filter = settingsService.get(message.getChatId());
        var sendTime = message.getSendTime();

        if (sendTime == null || sendTime.isBefore(filter.getUpdatedAt())) {
            searchRequest.setPage(0);
            message.setSource(PublishType.SEND);
        }

        filter.setRequestType(searchRequest.getRequestType());
        filter.setModifiedFrom(searchRequest.getStartDate());

        var source = searchRequest.getSource();
        if (searchRequest.getPage() == 0 && source == null) {
            return searchService.makeTrialRequest(filter, message.getChatId(), searchRequest)
                    .thenCompose(result -> handleTrialResult(message, result, parameters));
        }
        return findAndShow(message, parameters);
    }

    private CompletableFuture<Boolean> findAndShow(OutgoingMessage message, SearchActionParams parameters) {
        var searchRequest = parameters.getSearchParams();
        var filter = settingsService.get(message.getChatId());
        var page = searchRequest.getPage();
        var params = buildParams(filter, message, page);

        return searchService.searchWithOutcome(params, searchRequest.getSource())
                .thenApply(outcome -> {
                    var result = outcome.result();
                    var shownParams = parameters.getShownParams();
                    if (result.getNotEmptyResponseCount() == 0 && !shownParams.isShowIfEmpty()) {
                        return false;
                    }
                    handleResult(parameters, result, message);

                    var realMessageId = publisher.publish(message);
                    outcome.onPublished().accept(realMessageId, page + 1);
                    return true;
                });
    }

    private OutgoingMessage showSearchMessage(MessageData messageData) {
        var outgoing = new OutgoingMessage(messageData);
        VacanciesSearchMessageFormatter.fill(outgoing);
        var id = publisher.publish(outgoing);
        outgoing.setMessageId(id);
        return outgoing;
    }

    private CompletableFuture<Boolean> handleTrialResult(OutgoingMessage message, SearchResult result,
                                                         SearchActionParams parameters) {
        var shownParams = parameters.getShownParams();
        if (result.getNotEmptyResponseCount() == 0 && !shownParams.isShowIfEmpty()) {
            return CompletableFuture.completedFuture(false);
        }
        if (result.getTotalCount() > VACANCIES_COUNT_LIMIT) {
            resultMessage.fillSourceMessage(result, message);
            publisher.publish(message);
            return CompletableFuture.completedFuture(true);
        }
        return findAndShow(message, parameters);
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

    private VacanciesSearchParams buildParams(VacancySearchFilter filter, OutgoingMessage message, int page) {
        var messageId = message.getSource() == PublishType.UPDATE ?
                message.getMessageId() : null;

        return VacanciesSearchParams.builder()
                .filter(filter)
                .limit(VACANCIES_COUNT_LIMIT)
                .chatId(message.getChatId())
                .page(page)
                .messageId(messageId)
                .build();
    }
}
