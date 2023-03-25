create table test.scheduled_execution_logs (
  id BIGINT not null primary key ,
  task_name varchar(40) not null,
  task_instance varchar(40) not null,
  task_data blob,
  picked_by varchar(50),
  time_started timestamp(6) not null,
  time_finished timestamp(6) not null,
  succeeded BOOLEAN not null,
  duration_ms BIGINT not null,
  exception_class      varchar(1000),
  exception_message    blob,
  exception_stacktrace blob

  INDEX stl_started_idx (time_started);
  INDEX stl_task_name_idx (task_name);
  INDEX stl_exception_class_idx (exception_class);
)
