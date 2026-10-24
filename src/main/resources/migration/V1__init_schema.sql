-- ==========================================================
-- 1. Таблицы предметной области
-- ==========================================================

CREATE TABLE ingredients (
                             id          BIGSERIAL PRIMARY KEY,
                             name        VARCHAR(100) NOT NULL UNIQUE,
                             unit        VARCHAR(20)  NOT NULL,
                             stock       NUMERIC(15, 4) NOT NULL,
                             CONSTRAINT chk_stock_non_negative CHECK (stock >= 0)
);

CREATE TABLE recipes (
                         id           BIGSERIAL PRIMARY KEY,
                         product_name VARCHAR(200) NOT NULL
);

-- Связь многие-ко-многим между Recipe и Ingredient (с количеством)
CREATE TABLE recipe_ingredients (
                                    id             BIGSERIAL PRIMARY KEY,
                                    recipe_id      BIGINT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
                                    ingredient_id  BIGINT NOT NULL REFERENCES ingredients(id),
                                    quantity       NUMERIC(15, 4) NOT NULL,
                                    CONSTRAINT chk_quantity_positive CHECK (quantity > 0),
                                    CONSTRAINT uq_recipe_ingredient UNIQUE (recipe_id, ingredient_id)
);

CREATE TABLE users (
                       id    BIGSERIAL PRIMARY KEY,
                       name  VARCHAR(100) NOT NULL,
                       email VARCHAR(200) NOT NULL UNIQUE
);

CREATE TABLE production_batches (
                                    id         BIGSERIAL PRIMARY KEY,
                                    recipe_id  BIGINT NOT NULL REFERENCES recipes(id),
                                    quantity   INTEGER NOT NULL,
                                    status     VARCHAR(20) NOT NULL,
                                    CONSTRAINT chk_batch_quantity_positive CHECK (quantity > 0),
                                    CONSTRAINT chk_batch_status CHECK (status IN ('PLANNED','IN_PROGRESS','DONE','CANCELLED'))
);

CREATE TABLE baking_requests (
                                 id          BIGSERIAL PRIMARY KEY,
                                 customer_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
                                 recipe_id   BIGINT NOT NULL REFERENCES recipes(id),
                                 quantity    INTEGER NOT NULL,
                                 batch_id    BIGINT REFERENCES production_batches(id) ON DELETE SET NULL,
                                 status      VARCHAR(20) NOT NULL,
                                 created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
                                 CONSTRAINT chk_request_quantity_positive CHECK (quantity > 0),
                                 CONSTRAINT chk_request_status CHECK (status IN ('PENDING','APPROVED','READY','CANCELLED'))
);

CREATE INDEX idx_batches_recipe_status ON production_batches(recipe_id, status);
CREATE INDEX idx_requests_batch ON baking_requests(batch_id);

-- ==========================================================
-- 2. Пользователь-мигратор (для Flyway) — уже создан вручную
--    (см. инструкцию ниже). Здесь только проверяем, что он есть.
-- ==========================================================

-- ==========================================================
-- 3. Пользователь приложения с ОГРАНИЧЕННЫМИ правами (только DML)
-- ==========================================================
-- ВАЖНО: этот блок выполняется от имени суперпользователя / migrator.
-- Если вы создаёте БД вручную — выполните эти команды в psql от postgres.

DO $$
BEGIN
    -- Создаём пользователя, если его ещё нет
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'bakery_app_user') THEN
CREATE ROLE bakery_app_user WITH LOGIN PASSWORD 'app_pass_123';
END IF;
END
$$;

GRANT CONNECT ON DATABASE bakery_db TO bakery_app_user;
GRANT USAGE ON SCHEMA public TO bakery_app_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO bakery_app_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO bakery_app_user;

-- КЛЮЧЕВОЕ: права на таблицы, которые будут созданы ПОТОМ
ALTER DEFAULT PRIVILEGES IN SCHEMA public
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO bakery_app_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public
    GRANT USAGE, SELECT ON SEQUENCES TO bakery_app_user;

-- ==========================================================
-- 4. Тестовые данные
-- ==========================================================
INSERT INTO users (name, email) VALUES
                                    ('Иван Петров', 'ivan@example.com'),
                                    ('Мария Сидорова', 'maria@example.com');

INSERT INTO ingredients (name, unit, stock) VALUES
                                                ('Мука пшеничная', 'kg', 100.0000),
                                                ('Сахар',          'kg', 50.0000),
                                                ('Дрожжи',         'g',  500.0000),
                                                ('Соль',           'g',  200.0000);

INSERT INTO recipes (product_name) VALUES ('Хлеб Бородинский');

INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity) VALUES
                                                                        (1, 1, 0.5000),   -- 500 г муки на 1 хлеб
                                                                        (1, 2, 0.0500),   -- 50 г сахара
                                                                        (1, 3, 10.0000),  -- 10 г дрожжей
                                                                        (1, 4, 8.0000);   -- 8 г соли