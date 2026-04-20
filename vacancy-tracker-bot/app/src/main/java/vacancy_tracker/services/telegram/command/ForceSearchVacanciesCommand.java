package vacancy_tracker.services.telegram.command;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import vacancy_tracker.model.vacancy.Vacancy;
import vacancy_tracker.services.telegram.message.MessageSender;
import vacancy_tracker.services.telegram.settings.SettingsService;
import vacancy_tracker.services.telegram.view.VacanciesMessageFormatter;
import vacancy_tracker.sources.superjob.service.vacancy.SuperJobVacanciesService;

import java.util.List;

public class ForceSearchVacanciesCommand extends SimpleMessageCommand {

    //todo Тут должен быть общий сервис, который будет искать вакансии во всех api
    private final SuperJobVacanciesService vacanciesService;
    private final SettingsService settingsService;
    private final VacanciesMessageFormatter messageFormatter;

    public ForceSearchVacanciesCommand(MessageSender sender, SettingsService settingsService,
                                       SuperJobVacanciesService vacanciesService,
                                       VacanciesMessageFormatter messageFormatter) {
        super(sender);
        this.settingsService = settingsService;
        this.vacanciesService = vacanciesService;
        this.messageFormatter = messageFormatter;
    }

    @Override
    public String getKey() {
        return "/get_now";
    }

    @Override
    public String getDescription() {
        return "Вывод всего и сразу";
    }

    @Override
    public void execute(Message message) {
        long id = message.getChatId();
        var filter = settingsService.getFilters(id);

        var future = vacanciesService.search(filter);
        future.thenAccept(vacancies -> send(vacancies, id));
    }

    private void send(List<Vacancy> vacancies, long chatId) {
        var text = messageFormatter.format(vacancies);
        sender.sendText(chatId, text, ParseMode.MARKDOWN);
    }
}
