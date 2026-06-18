# Vacancy Tracker Bot

Telegram-бот для агрегации и отслеживания вакансий из **SuperJob** и **TrudVsem**. Поддерживает настройку фильтров поиска и автоматические уведомления о новых вакансиях по расписанию.

## Возможности

- Поиск вакансий из двух источников: SuperJob и TrudVsem
- Фильтрация по ключевому слову, региону, городу, зарплате и опыту работы
- Автоматические уведомления о новых вакансиях с настраиваемым интервалом
- Интерактивная навигация с inline-клавиатурами и пагинацией
- Многошаговые диалоги для изменения настроек
## Стек

| Слой | Технология |
|---|---|
| Язык | Java 21 |
| Фреймворк | Spring Boot 3.2 |
| Telegram SDK | TelegramBots 10.0 |
| БД | PostgreSQL 16 + JPA/Hibernate |
| Миграции | Liquibase |
| Кэш | Redis 7 |
| HTTP-клиент | WebClient (Spring WebFlux) |
| Сборка | Gradle (Kotlin DSL) |

## Старт приложения

### Требования

- Docker и Docker Compose
- 
### Запуск

1. Клонировать репозиторий:
```bash
   git clone https://github.com/Zipprey12/VacancyTrackerBot
   cd vacancy-tracker-bot
```

2. Создать файл с переменными окружения:
```bash
   cp docker/example.env docker/.env
```

3. Заполнить `docker/.env`

### Переменные окружения

| Переменная | Описание                       |
|---|--------------------------------|
| `BOT_NAME` | Username бота (без @)          |
| `BOT_TOKEN` | Токен от @BotFather            |
| `SUPER_JOB_APP_ID` | ID приложения SuperJob         |
| `SUPER_JOB_SECRET` | Секретный ключ SuperJob API (https://api.superjob.ru/info/) |
| `POSTGRES_USER` | Пользователь PostgreSQL        |
| `POSTGRES_PASSWORD` | Пароль PostgreSQL              |

### Вариант А — Docker Compose

Требования: Docker и Docker Compose.

```bash
cd docker
docker compose up --build
```

При первом запуске Docker соберёт JAR внутри контейнера — это займёт несколько минут. При последующих запусках без изменений в коде:

```bash
docker compose up
```

Остановка:

```bash
docker compose down
```
---

### Вариант Б — IDEA + Docker (инфраструктура)

Требования: JDK 21, IDEA, Docker.

Поднять только PostgreSQL и Redis:

```bash
cd docker
docker compose up postgres redis
```

В IDEA открыть **Run → Edit Configurations** для `App.java` и добавить переменные окружения (указать файл .env)

Запустить `App.java` через IDEA

## Основные команды бота

| Команда | Описание |
|---|---|
| `/get_all` | Получить вакансии по текущим фильтрам |
| `/search_settings` | Настройки фильтров поиска |
| `/notification_settings` | Настройки уведомлений |
| `/reset_filters` | Сбросить фильтры |

## Настройки фильтров

Доступны через `/search_settings`:

- **Ключевое слово** — текстовый поиск по названию вакансии
- **Регион** — поиск по коду или названию региона
- **Город** — уточнение города внутри региона
- **Минимальная зарплата**
- **Максимальная зарплата**
- **Опыт работы** — в годах (дробные значения допустимы)
## Настройки уведомлений

Доступны через `/notification_settings`:

- **Включить/выключить** уведомления
- **Интервал** — произвольный: несколько часов, дней или раз в неделю в определённый день
- **Время** — время суток для отправки уведомлений
- **Уведомлять если нет новых вакансий** — опциональное
## Структура проекта

```
vacancy-tracker-bot/
├── Dockerfile
├── start.sh                        # скрипт запуска
├── docker/
│   ├── docker-compose.yml
│   ├── .env                        # секреты (не коммитить)
│   └── example.env
└── app/
    └── src/main/java/vacancy_tracker/
        ├── bot/                    # точка входа Telegram-бота
        ├── config/                 # конфигурация Spring
        ├── model/                  # доменные модели, DTO, entities
        ├── repository/             # JPA-репозитории
        ├── services/
        │   ├── api/                # интеграция с внешними API
        │   ├── telegram/
        │   │   ├── actions/        # Action-классы (execute + handleWithParameter)
        │   │   ├── command/        # команды бота и стратегии выполнения
        │   │   ├── callback/       # обработчики inline-кнопок
        │   │   ├── notification/   # планировщик уведомлений
        │   │   ├── session/        # управление сессиями пользователей
        │   │   ├── settings/       # сервисы настроек
        │   │   └── view/           # форматирование сообщений, клавиатуры
        │   └── util/               # DateUtil, StringUtil
        └── sources/
            ├── superjob/           # клиент SuperJob API
            └── trudvsem/           # клиент ТрудВсем API
```

## Архитектурные решения

**Стратегии выполнения команд.** Каждая команда принимает `ExecutionStrategy` — `SyncExecutionStrategy`, `AsyncExecutionStrategy` или `SequentialAsyncExecutionStrategy`. Последняя гарантирует строгий порядок выполнения задач одного пользователя без блокировки других пользователей, что критично для команд, изменяющих настройки.

**Перехватчики ввода.** `InputInterceptingCommand` позволяет реализовать многошаговые диалоги: после вызова команды бот ожидает текстовый ввод от пользователя и обрабатывает его через типизированный `InputInterceptor<T>` (FloatInterceptor, TimeInterceptor, DurationInterceptor и др.).

**Очередь уведомлений.** Реализована на Redis Sorted Set: каждый пользователь хранится с `score = unix timestamp` следующего уведомления. Планировщик раз в секунду выбирает просроченные записи и обрабатывает их асинхронно через `NotificationProcessor`.

**Кэширование.** Сессии, настройки фильтров и уведомлений кэшируются в Redis с раздельными TTL. Сериализация через Jackson с поддержкой `java.time` типов.