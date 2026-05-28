package vacancy_tracker.services.telegram.handlers;

import vacancy_tracker.model.telegram.view.Identifiable;

public interface IdentifiableDataHandler<T> extends ParametrizedDataHandler<T>, Identifiable {
}
