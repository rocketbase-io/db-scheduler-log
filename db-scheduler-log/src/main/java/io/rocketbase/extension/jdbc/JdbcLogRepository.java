/**
 * Copyright (C) Marten Prieß
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rocketbase.extension.jdbc;

import com.github.kagkarlsson.jdbc.JdbcRunner;
import com.github.kagkarlsson.jdbc.SQLRuntimeException;
import com.github.kagkarlsson.scheduler.jdbc.AutodetectJdbcCustomization;
import com.github.kagkarlsson.scheduler.jdbc.JdbcCustomization;
import com.github.kagkarlsson.scheduler.serializer.Serializer;
import io.rocketbase.extension.ExecutionLog;
import io.rocketbase.extension.LogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.time.Duration;

public class JdbcLogRepository implements LogRepository {

    public static final String DEFAULT_TABLE_NAME = "scheduled_execution_logs";

    private static final Logger LOG = LoggerFactory.getLogger(JdbcLogRepository.class);
    private final JdbcRunner jdbcRunner;
    private final Serializer serializer;
    private final String tableName;
    private final JdbcCustomization jdbcCustomization;
    private final IdProvider idProvider;


    public JdbcLogRepository(DataSource dataSource, Serializer serializer, String tableName, IdProvider idProvider) {
        this(tableName, new JdbcRunner(dataSource, true), serializer, new AutodetectJdbcCustomization(dataSource), idProvider);
    }

    public JdbcLogRepository(String tableName, JdbcRunner jdbcRunner, Serializer serializer, JdbcCustomization jdbcCustomization, IdProvider idProvider) {
        this.tableName = tableName;
        this.jdbcRunner = jdbcRunner;
        this.serializer = serializer;
        this.jdbcCustomization = jdbcCustomization;
        this.idProvider = idProvider;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public boolean createIfNotExists(ExecutionLog log) {
        try {
            jdbcRunner.execute(
                "insert into " + tableName + "(id, task_name, task_instance, task_data, picked_by, time_started, time_finished, succeeded, duration_ms) values(?, ?, ?, ?, ?, ?, ?, ?, ?)",
                (PreparedStatement p) -> {
                    p.setLong(1, idProvider.nextId());
                    p.setString(2, log.taskInstance.getTaskName());
                    p.setString(3, log.taskInstance.getId());
                    if (log.taskInstance.getData() != null || serializer == null) {
                        p.setObject(4, null);
                    } else {
                        p.setObject(4, serializer.serialize(log.taskInstance.getData()));
                    }
                    p.setString(5, log.pickedBy);
                    jdbcCustomization.setInstant(p, 6, log.timeStarted);
                    jdbcCustomization.setInstant(p, 7, log.timeFinished);
                    p.setBoolean(8, log.succeeded);
                    p.setLong(9, Duration.between(log.timeStarted, log.timeFinished).toMillis());
                });
            return true;
        } catch (SQLRuntimeException e) {
            LOG.debug("Exception when inserting execution-log. Assuming it to be a constraint violation.", e);
            return false;
        }
    }

}
