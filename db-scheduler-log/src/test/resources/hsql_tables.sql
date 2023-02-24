create table scheduled_tasks (
    id BIGINT primary key  not null,
    task_name varchar(100),
    task_instance varchar(100),
    task_data blob,
    picked_by varchar(50),
    time_started TIMESTAMP WITH TIME ZONE,
    time_finished TIMESTAMP WITH TIME ZONE,
    duration_ms BIGINT,
)
