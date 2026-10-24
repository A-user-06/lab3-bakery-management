\# Bakery Management System



Приложение для управления производством пекарни. Переведено на реляционную базу данных PostgreSQL с использованием Spring Data JPA.



\## Стек технологий

\* Java 17+, Spring Boot 3.5.5+

\* PostgreSQL

\* Spring Data JPA (Hibernate)

\* Maven



\## База данных и безопасность

\* \*\*Безопасность:\*\* Приложение работает под пользователем `bakery\_app\_user`, у которого есть права \*\*только на DML\*\* (SELECT, INSERT, UPDATE, DELETE). Права на изменение схемы (DDL) у приложения отсутствуют.

\* \*\*Конфигурация:\*\* Пароли и URL вынесены в файл `.env` (добавлен в `.gitignore`).

\* \*\*Ограничения целостности:\*\* Настроены на уровне БД (CHECK `stock >= 0`, `quantity > 0`, Foreign Keys).



\##  Сущности

1\. \*\*User\*\* — клиент.

2\. \*\*Ingredient\*\* — ингредиент (остаток на складе).

3\. \*\*Recipe\*\* — рецепт. Состав вынесен в таблицу `RecipeIngredient` (связь многие-ко-многим).

4\. \*\*ProductionBatch\*\* — партия производства.

5\. \*\*BakingRequest\*\* — заявка клиента.



\##  Реализованные бизнес-операции

1\. \*\*Одобрение заявки (`POST /api/baking-requests/{id}/approve`)\*\* — проверяет остатки с учетом резерва и создает партию `PLANNED`. \*(Затрагивает BakingRequest, ProductionBatch, Ingredient)\*

2\. \*\*Запуск производства (`POST /api/production-batches/{id}/start`)\*\* — переводит партию в `IN\_PROGRESS` и списывает ингредиенты со склада. \*(Затрагивает ProductionBatch, Ingredient)\*

3\. \*\*Завершение производства (`POST /api/production-batches/{id}/complete`)\*\* — переводит партию в `DONE`, а связанные заявки в `READY`. \*(Затрагивает ProductionBatch, BakingRequest)\*

4\. \*\*Отмена заявки (`POST /api/baking-requests/{id}/cancel`)\*\* — запрещена, если производство началось. \*(Затрагивает BakingRequest, ProductionBatch)\*

5\. \*\*Безопасное изменение рецепта (`PUT /api/recipes/{id}/composition`)\*\* — запрещает менять рецепт при наличии активных партий.



\## Как запустить

1\. Установите PostgreSQL и создайте БД `bakery\_db`.

2\. Создайте файл `.env` в корне проекта с настройками подключения.

3\. Запустите приложение: `mvn spring-boot:run` (или через кнопку Run в IDEA).

4\. Для тестирования импортируйте `bakery\_collection.json` в Postman.

