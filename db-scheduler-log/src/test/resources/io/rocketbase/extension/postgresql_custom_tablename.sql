create table custom_tablename_logs
(
    id             BIGINT                   not null primary key,
    task_name      text                     not null,
    task_instance  text                     not null,
    task_data      bytea,
    picked_by      text,
    time_started   timestamp with time zone not null,
    time_finished  timestamp with time zone not null,
    succeeded      BOOLEAN                  not null,
    duration_ms    BIGINT                   not null,
    exception_data bytea
);

CREATE INDEX stl_custom_started_idx ON custom_tablename_logs (time_started);
CREATE INDEX stl_custom_task_name_idx ON custom_tablename_logs (task_name);
