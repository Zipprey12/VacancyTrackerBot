package vacancy_tracker.model.telegram.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommandCategory {

    MAIN("Основные"),
    OTHER("Другие"),
    FILTER("Настройка поиска"),
    NOTIFICATION("Настройка уведомлений");

    private final String text;

}
