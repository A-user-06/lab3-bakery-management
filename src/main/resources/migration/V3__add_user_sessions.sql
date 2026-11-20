CREATE TABLE user_sessions (
                               id UUID PRIMARY KEY,
                               email VARCHAR(255) NOT NULL,
                               device_id VARCHAR(255),
                               access_token TEXT NOT NULL,
                               refresh_token TEXT NOT NULL UNIQUE,
                               access_token_expiry TIMESTAMP WITH TIME ZONE NOT NULL,
                               refresh_token_expiry TIMESTAMP WITH TIME ZONE NOT NULL,
                               status VARCHAR(20) NOT NULL,
                               CONSTRAINT fk_user_sessions_user FOREIGN KEY (email) REFERENCES users(email)
);

-- Права для сервисной роли (замени на реальную роль из V1/V2)
GRANT SELECT, INSERT, UPDATE ON user_sessions TO <bakery_app_user>;