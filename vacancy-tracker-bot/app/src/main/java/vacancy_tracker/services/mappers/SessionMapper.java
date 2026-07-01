package vacancy_tracker.services.mappers;

import org.springframework.stereotype.Component;
import vacancy_tracker.model.persistence.SessionEntity;
import vacancy_tracker.model.telegram.session.UserSessionContext;

@Component
public class SessionMapper {

    public UserSessionContext toDomain(SessionEntity entity) {
        var context = new UserSessionContext(entity.getChatId());
        var key = entity.getInterceptorCommandKey();
        context.setInputHandlerKey(key);
        return context;
    }

    public SessionEntity toEntity(UserSessionContext context) {
        var entity = new SessionEntity();
        entity.setChatId(context.getChatId());
        var interceptorKey = context.getInputHandlerKey();
        entity.setInterceptorCommandKey(interceptorKey);
        return entity;
    }
}
