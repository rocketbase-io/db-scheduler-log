# db-scheduler-log

![build status](https://github.com/rocketbase-io/db-scheduler-log/workflows/build/badge.svg)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.rocketbase.extension/db-scheduler-log/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.rocketbase.extension/db-scheduler-log)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

## Getting started

1. Add maven dependency

```xml

<dependency>
    <groupId>io.rocketbase.extension</groupId>
    <artifactId>db-scheduler-log</artifactId>
    <version>${version}</version>
</dependency>
```

2. Create the `scheduled_execution_logs` table in your database-schema. See table definition
   for [postgresql](db-scheduler-log/src/test/resources/postgresql_tables.sql), [oracle](db-scheduler-log/src/test/resources/oracle_tables.sql), [mssql](db-scheduler-log/src/test/resources/mssql_tables.sql)
   or [mysql](db-scheduler-log/src/test/resources/mysql_tables.sql).

> :mega: It's highly recommended to create the log-table with daily partitions based on time_started with a proper
> retention when you have a huge amount of running tasks... otherwise you could run out of disk-space quite soon.

3. Customize the scheduler to use extended StatsRegistry.

```java
final JdbcLogRepository jdbcLogRepository=new JdbcLogRepository(dataSource,new JavaSerializer(),JdbcLogRepository.DEFAULT_TABLE_NAME,new Snowflake());

final Scheduler scheduler=Scheduler
        .create(dataSource)
        .startTasks(hourlyTask)
        .threads(5)
        .statsRegistry(new LogStatsPlainRegistry(jdbcLogRepository))
        .build();
```

## Spring Boot usage

For Spring Boot applications, there is a starter `db-scheduler-log-spring-boot-starter` making the scheduler-log-wiring
very simple.

### Prerequisites

- An existing Spring Boot application
- A working `DataSource` with schema initialized. (In the example HSQLDB is used and schema is automatically applied.)

### Getting started

1. Add the following Maven dependency
    ```xml
    <dependency>
        <groupId>io.rocketbase.extension</groupId>
        <artifactId>db-scheduler-log-spring-boot-starter</artifactId>
        <version>${version}</version>
    </dependency>
    ```
   **NOTE**: This includes the db-scheduler-spring-boot-starter dependency itself.
2. Do configuration explained on db-scheduler...
   3Run the app.

### Configuration options

Configuration is mainly done via `application.properties`. Configuration of table-name is done by properties.

```
# application.properties example showing default values

db-scheduler-log.enabled=true
db-scheduler-log.table-name=scheduled_execution_logs
```
