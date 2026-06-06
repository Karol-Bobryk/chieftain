CREATE TABLE roles_dictionary
(
    pk_role_id SERIAL PRIMARY KEY,
    role_name  VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE group_user_permission_dictionary
(
    pk_permission_id SERIAL PRIMARY KEY,
    permission_name  VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE task_status_dictionary
(
    pk_task_status_id SERIAL PRIMARY KEY,
    status_name       VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE log_severity_dictionary
(
    pk_log_severity_id SERIAL PRIMARY KEY,
    log_severity_name  VARCHAR(255) NOT NULL UNIQUE
);


CREATE TABLE organizations
(
    pk_organization_id UUID PRIMARY KEY,
    name               VARCHAR(255) NOT NULL,
    join_token         VARCHAR(255) NOT NULL UNIQUE,
    blocked            BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at         TIMESTAMP(6) NOT NULL DEFAULT NOW()
);

CREATE TABLE users
(
    pk_user_id         UUID PRIMARY KEY,
    fk_organization_id UUID REFERENCES organizations (pk_organization_id),
    email_address      VARCHAR(255) UNIQUE NOT NULL,
    secret_hash        VARCHAR(255)        NOT NULL,
    name               VARCHAR(255)        NOT NULL,
    surname            VARCHAR(255)        NOT NULL,
    job_title          VARCHAR(255)        NOT NULL,
    blocked            BOOLEAN             NOT NULL DEFAULT FALSE,
    joined_at          TIMESTAMP(6)        NOT NULL DEFAULT NOW(),
    fk_role_id         INTEGER             NOT NULL REFERENCES roles_dictionary (pk_role_id)
);

CREATE TABLE groups
(
    pk_group_id        UUID PRIMARY KEY,
    fk_organization_id UUID         NOT NULL REFERENCES organizations (pk_organization_id) ON DELETE CASCADE,
    name               VARCHAR(255) NOT NULL,
    created_at         TIMESTAMP(6) NOT NULL DEFAULT NOW()
);

CREATE TABLE group_members
(
    fk_group_id UUID NOT NULL REFERENCES groups (pk_group_id) ON DELETE CASCADE,
    fk_user_id  UUID NOT NULL REFERENCES users (pk_user_id) ON DELETE CASCADE,
    PRIMARY KEY (fk_group_id, fk_user_id)
);

CREATE TABLE group_privileges
(
    fk_group_id UUID NOT NULL REFERENCES groups (pk_group_id) ON DELETE CASCADE,
    fk_user_id  UUID NOT NULL REFERENCES users (pk_user_id) ON DELETE CASCADE,
    PRIMARY KEY (fk_group_id, fk_user_id)
);

CREATE TABLE group_privilege_permissions
(
    fk_group_id      UUID    NOT NULL REFERENCES groups (pk_group_id) ON DELETE CASCADE,
    fk_user_id       UUID    NOT NULL REFERENCES users (pk_user_id) ON DELETE CASCADE,
    fk_permission_id INTEGER NOT NULL REFERENCES group_user_permission_dictionary (pk_permission_id),
    PRIMARY KEY (fk_group_id, fk_user_id, fk_permission_id)
);

CREATE TABLE tasks
(
    pk_task_id         UUID PRIMARY KEY,
    fk_creator_user_id UUID         NOT NULL REFERENCES users (pk_user_id) ON DELETE RESTRICT,
    parent_task_id     UUID REFERENCES tasks (pk_task_id) ON DELETE CASCADE,
    fk_group_id        UUID         NOT NULL REFERENCES groups (pk_group_id) ON DELETE CASCADE,
    name               VARCHAR(255) NOT NULL,
    created_at         TIMESTAMP(6) NOT NULL DEFAULT NOW(),
    done_at            TIMESTAMP(6),
    description        VARCHAR(255),
    deadline           TIMESTAMP(6),
    fk_status_id       INTEGER      NOT NULL REFERENCES task_status_dictionary (pk_task_status_id),
    version            BIGINT       NOT NULL DEFAULT 0
);

CREATE TABLE task_assignees
(
    fk_task_id          UUID NOT NULL REFERENCES tasks (pk_task_id) ON DELETE CASCADE,
    fk_assignee_user_id UUID NOT NULL REFERENCES users (pk_user_id) ON DELETE CASCADE,
    PRIMARY KEY (fk_task_id, fk_assignee_user_id)
);

CREATE TABLE users_awaiting_acceptance
(
    fk_organization_id UUID NOT NULL REFERENCES organizations (pk_organization_id) ON DELETE CASCADE,
    fk_user_id         UUID NOT NULL REFERENCES users (pk_user_id) ON DELETE CASCADE,
    PRIMARY KEY (fk_organization_id, fk_user_id)
);

CREATE TABLE user_logs
(
    pk_log_id   BIGSERIAL PRIMARY KEY,
    fk_user_id  UUID         NOT NULL REFERENCES users (pk_user_id) ON DELETE CASCADE,
    fk_log_severity_id  INTEGER  NOT NULL REFERENCES log_severity_dictionary(pk_log_severity_id),
    action      VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    created_at  TIMESTAMP(6) NOT NULL DEFAULT NOW()
);

CREATE TABLE organization_logs
(
    pk_log_id          BIGSERIAL PRIMARY KEY,
    fk_organization_id UUID         NOT NULL REFERENCES organizations (pk_organization_id) ON DELETE CASCADE,
    fk_log_severity_id  INTEGER  NOT NULL REFERENCES log_severity_dictionary(pk_log_severity_id),
    created_at         TIMESTAMP(6) NOT NULL DEFAULT NOW(),
    action             VARCHAR(256) NOT NULL,
    description        VARCHAR(255)
);

CREATE TABLE group_logs
(
    pk_log_id   BIGSERIAL PRIMARY KEY,
    fk_group_id UUID         NOT NULL REFERENCES groups (pk_group_id) ON DELETE CASCADE,
    fk_log_severity_id  INTEGER  NOT NULL REFERENCES log_severity_dictionary(pk_log_severity_id),
    created_at  TIMESTAMP(6) NOT NULL DEFAULT NOW(),
    action      VARCHAR(256) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE group_privilege_logs
(
    pk_log_id   BIGSERIAL PRIMARY KEY,
    fk_group_id UUID         NOT NULL REFERENCES groups (pk_group_id) ON DELETE CASCADE,
    fk_user_id  UUID         NOT NULL REFERENCES users (pk_user_id) ON DELETE CASCADE,
    fk_log_severity_id  INTEGER  NOT NULL REFERENCES log_severity_dictionary(pk_log_severity_id),
    created_at  TIMESTAMP(6) NOT NULL DEFAULT NOW(),
    action      VARCHAR(256) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE task_logs
(
    pk_log_id   BIGSERIAL PRIMARY KEY,
    fk_task_id  UUID         NOT NULL REFERENCES tasks (pk_task_id) ON DELETE CASCADE,
    fk_log_severity_id  INTEGER  NOT NULL REFERENCES log_severity_dictionary(pk_log_severity_id),
    created_at  TIMESTAMP(6) NOT NULL DEFAULT NOW(),
    action      VARCHAR(256) NOT NULL,
    description VARCHAR(255)
);