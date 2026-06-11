DROP VIEW IF EXISTS v_system_logs;
CREATE OR REPLACE VIEW v_system_logs AS

SELECT
    'USER_' || ul.pk_log_id AS log_id,
    'USER' AS domain,
    ul.fk_user_id AS entity_id,
    sd.log_severity_name AS severity,
    ul.action,
    ul.description,
    ul.created_at
FROM user_logs ul
JOIN log_severity_dictionary sd ON ul.fk_log_severity_id = sd.pk_log_severity_id

UNION ALL

SELECT
    'ORGANIZATION_' || ol.pk_log_id AS log_id,
    'ORGANIZATION' AS domain,
    ol.fk_organization_id AS entity_id,
    sd.log_severity_name AS severity,
    ol.action,
    ol.description,
    ol.created_at
FROM organization_logs ol
JOIN log_severity_dictionary sd ON ol.fk_log_severity_id = sd.pk_log_severity_id

UNION ALL

SELECT
    'GROUP_' || gl.pk_log_id AS log_id,
    'GROUP' AS domain,
    gl.fk_group_id AS entity_id,
    sd.log_severity_name AS severity,
    gl.action,
    gl.description,
    gl.created_at
FROM group_logs gl
JOIN log_severity_dictionary sd ON gl.fk_log_severity_id = sd.pk_log_severity_id

UNION ALL

SELECT
    'PRIVILEGE_' || gpl.pk_log_id AS log_id,
    'PRIVILEGE' AS domain,
    gpl.fk_user_id AS entity_id,
    sd.log_severity_name AS severity,
    gpl.action,
    gpl.description,
    gpl.created_at
FROM group_privilege_logs gpl
JOIN log_severity_dictionary sd ON gpl.fk_log_severity_id = sd.pk_log_severity_id

UNION ALL

SELECT
    'TASK_' || tl.pk_log_id AS log_id,
    'TASK' AS domain,
    tl.fk_task_id AS entity_id,
    sd.log_severity_name AS severity,
    tl.action,
    tl.description,
    tl.created_at
FROM task_logs tl
JOIN log_severity_dictionary sd ON tl.fk_log_severity_id = sd.pk_log_severity_id;