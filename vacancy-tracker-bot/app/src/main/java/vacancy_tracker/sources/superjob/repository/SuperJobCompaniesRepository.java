package vacancy_tracker.sources.superjob.repository;


import org.springframework.stereotype.Repository;
import vacancy_tracker.model.vacancy.entity.Company;

import java.util.HashMap;
import java.util.Optional;

//Класс нужен для снижения количества запросов к Api
@Repository
public class SuperJobCompaniesRepository {

    private final HashMap<Integer, Company> companies = new HashMap<>();

    public Optional<Company> findById(int id) {
        return Optional.ofNullable(companies.get(id));
    }

    public Company save(Company company) {
        companies.put(company.getId(), company);
        return company;
    }
}
