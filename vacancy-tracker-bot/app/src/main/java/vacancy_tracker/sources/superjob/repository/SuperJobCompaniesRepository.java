package vacancy_tracker.sources.superjob.repository;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import vacancy_tracker.model.api.Company;

import java.util.HashMap;
import java.util.Optional;

//Класс нужен для снижения количества запросов к Api
@Repository
@Slf4j
public class SuperJobCompaniesRepository {

    private final HashMap<Integer, Company> companies = new HashMap<>();

    public Optional<Company> findById(int id) {
        return Optional.ofNullable(companies.get(id));
    }

    public Company save(Company company) {
        log.debug("Сохранение компании: {} в репозиторий", company);
        companies.put(company.getId(), company);
        return company;
    }
}
