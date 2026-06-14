CREATE FUNCTION cascade_cancel_subtasks() RETURNS TRIGGER AS $subtask_cascade$
BEGIN
    IF
        NEW.fk_status_id != OLD.fk_status_id THEN
        IF (SELECT status_name FROM task_status_dictionary
            WHERE pk_task_status_id = NEW.fk_status_id) = 'CANCELLED' THEN
                UPDATE tasks
                SET fk_status_id = NEW.fk_status_id
                WHERE parent_task_id = NEW.pk_task_id;
        END IF;
    END IF;
    RETURN NEW;
END;
$subtask_cascade$
LANGUAGE plpgsql;

CREATE TRIGGER trg_cascade_cancel_subtasks
    AFTER UPDATE OF fk_status_id ON tasks
    FOR EACH ROW EXECUTE FUNCTION cascade_cancel_subtasks();