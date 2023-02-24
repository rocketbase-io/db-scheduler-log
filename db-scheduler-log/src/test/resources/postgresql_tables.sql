create table scheduled_tasks_log
(
    id            BIGINT                   not null primary key,
    task_name     text                     not null,
    task_instance text                     not null,
    task_data     bytea,
    picked_by     text,
    time_started  timestamp with time zone not null,
    time_finished timestamp with time zone not null,
    duration_ms   BIGINT                   not null
);

CREATE INDEX stl_started_idx ON scheduled_tasks_log (started);
CREATE INDEX stl_task_name_idx ON scheduled_tasks_log (task_name);
