-- Добавляем колонки для безопасности в таблицу users
ALTER TABLE users ADD COLUMN IF NOT EXISTS password VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS role VARCHAR(20) DEFAULT 'USER';
ALTER TABLE users ADD COLUMN IF NOT EXISTS active BOOLEAN DEFAULT TRUE;

-- Обновляем старых пользователей, чтобы избежать NULL
UPDATE users SET password = '$2a$10$rZ2n5nO5P5y6B.x7B.x7BeJ2v5nO5P5y6B.x7B.x7B.x7B.x7B.x7B' WHERE password IS NULL;
UPDATE users SET role = 'USER' WHERE role IS NULL;
UPDATE users SET active = TRUE WHERE active IS NULL;

-- Делаем поля обязательными
ALTER TABLE users ALTER COLUMN password SET NOT NULL;
ALTER TABLE users ALTER COLUMN role SET NOT NULL;
ALTER TABLE users ALTER COLUMN active SET NOT NULL;