package vacancy_tracker.services.telegram.command.publishers;

import vacancy_tracker.model.telegram.dto.OutgoingMessage;

public interface MessagePublisher {

    void publish(OutgoingMessage message);

}
