INSERT INTO roles_dictionary(role_name)
VALUES ('SITE_ADMIN'),
       ('OWNER'),
       ('TASK_MASTER'),
       ('GROUP_USER'),
       ('GUEST');

INSERT INTO group_user_permission_dictionary(permission_name)
VALUES ('ADD_TASK'),
       ('REMOVE_TASK'),
       ('EDIT_TASK'),
       ('ADD_USER_TO_GROUP'),
       ('REMOVE_USER_FROM_GROUP');

INSERT INTO task_status_dictionary(status_name)
VALUES ('CREATED'),
       ('IN_PROGRESS'),
       ('FINISHED'),
       ('CANCELLED');

INSERT INTO log_severity_dictionary(log_severity_name)
VALUES('INFO'),
      ('WARNING'),
      ('ERROR'),
      ('CRITICAL_ERROR');