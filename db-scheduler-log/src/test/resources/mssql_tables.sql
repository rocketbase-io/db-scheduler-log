create table scheduled_execution_logs (
  id BIGINT not null primary key,
  task_name varchar(250) not null,
  task_instance varchar(250) not null,
  task_data  nvarchar(max),
  picked_by text,
  time_started datetimeoffset ,
  time_finished datetimeoffset ,
  succeeded  bit,
  duration_ms BIGINT not null,
  exception_class      varchar(1000),
  exception_message    nvarchar(max),
  exception_stacktrace nvarchar(max)

  INDEX stl_started_idx (time_started),
  INDEX stl_task_name_idx (task_name),
  INDEX stl_exception_class_idx (exception_class)
)
