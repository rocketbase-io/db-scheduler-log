create table scheduled_tasks_log
(
    id            NUMBER       not null primary key,
    task_name     varchar(100),
    task_instance varchar(100),
    task_data     blob,
    picked_by     varchar(50),
    time_started  TIMESTAMP(6) not null,
    time_finished TIMESTAMP(6) not null,
    succeeded     NUMBER(1, 0),
    duration_ms   NUMBER       not null
)

