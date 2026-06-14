CREATE INDEX idx_group_members_user_id
ON group_members(fk_user_id);

CREATE INDEX idx_tasks_group_id
ON tasks(fk_group_id);

CREATE INDEX idx_tasks_parent_id
ON tasks(parent_task_id);

CREATE INDEX idx_task_assignees_user_id
ON task_assignees(fk_assignee_user_id);

CREATE INDEX idx_users_organization_id
ON users(fk_organization_id);

CREATE INDEX idx_blacklisted_tokens_expiration
ON blacklisted_refresh_tokens(expiration_time);

CREATE INDEX idx_groups_organization_id
ON groups(fk_organization_id);

CREATE INDEX idx_tasks_deadline
ON tasks(deadline);

CREATE INDEX idx_awaiting_user_id
ON users_awaiting_acceptance(fk_user_id);

CREATE INDEX idx_tasks_started_at
ON tasks(started_at);
