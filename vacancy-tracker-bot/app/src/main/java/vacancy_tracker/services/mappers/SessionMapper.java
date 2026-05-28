package vacancy_tracker.services.mappers;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import vacancy_tracker.model.telegram.UserSessionContext;
import vacancy_tracker.model.telegram.entities.SessionEntity;
import vacancy_tracker.services.telegram.command.CommandsService;

@Component
public class SessionMapper {

    private final CommandsService commandsService;

    public SessionMapper(@Lazy CommandsService commandsService) {
        this.commandsService = commandsService;
    }

    public UserSessionContext toDomain(SessionEntity entity) {
        var context = new UserSessionContext(entity.getChatId());
        var key = entity.getInterceptorCommandKey();
        if (key != null) {
            commandsService.getInterceptorByCommandKey(key)
                    .ifPresent(context::setInputInterceptor);
        }
        return context;
    }

    public SessionEntity toEntity(UserSessionContext context) {
        var entity = new SessionEntity();
        entity.setChatId(context.getChatId());
        var interceptor = context.getInputInterceptor();
        if (interceptor != null && interceptor.getDataHandler() != null) {
            entity.setInterceptorCommandKey(interceptor.getDataHandler().getKey());
        }
        return entity;
    }
}
