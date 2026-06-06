CREATE TABLE blacklisted_refresh_tokens
(
    jti             UUID PRIMARY KEY,
    expiration_time TIMESTAMP(6) NOT NULL
);