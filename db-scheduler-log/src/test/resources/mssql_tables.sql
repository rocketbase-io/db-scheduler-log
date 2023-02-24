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
  INDEX stl_started_idx (time_started),
  INDEX stl_task_name_idx (task_name)
)
