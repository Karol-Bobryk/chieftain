CREATE OR REPLACE FUNCTION set_task_done_at() RETURNS TRIGGER AS $task_stat$

DECLARE
       finished_status_id INTEGER;
BEGIN
     SELECT pk_task_status_id INTO finished_status_id
     FROM task_status_dictionary
     WHERE status_name = 'FINISHED';

      IF NEW.fk_status_id = finished_status_id
      THEN
         NEW.done_at = NOW();
      ELSE
         NEW.done_at = null;
      END IF;
      RETURN NEW;
END;
$task_stat$ LANGUAGE plpgsql;

CREATE TRIGGER trg_task_done_at
    BEFORE INSERT OR UPDATE OF fk_status_id ON tasks
    FOR EACH ROW EXECUTE FUNCTION set_task_done_at();



