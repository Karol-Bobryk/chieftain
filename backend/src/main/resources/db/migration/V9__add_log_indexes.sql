CREATE INDEX idx_user_logs_severity_created
    ON user_logs (fk_log_severity_id, created_at DESC);

CREATE INDEX idx_task_logs_severity_created
    ON task_logs (fk_log_severity_id, created_at DESC);

CREATE INDEX idx_group_logs_severity_created
    ON group_logs (fk_log_severity_id, created_at DESC);

CREATE INDEX idx_group_privilege_logs_severity_created
    ON group_privilege_logs (fk_log_severity_id, created_at DESC);

CREATE INDEX idx_organization_logs_severity_created
    ON organization_logs (fk_log_severity_id, created_at DESC);