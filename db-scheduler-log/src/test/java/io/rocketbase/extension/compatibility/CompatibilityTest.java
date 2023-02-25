package io.rocketbase.extension.compatibility;

import com.github.kagkarlsson.scheduler.serializer.JavaSerializer;
import com.github.kagkarlsson.scheduler.task.Execution;
import com.github.kagkarlsson.scheduler.task.ExecutionComplete;
import com.github.kagkarlsson.scheduler.task.TaskInstance;
import io.rocketbase.extension.DbUtils;
import io.rocketbase.extension.ExecutionLog;
import io.rocketbase.extension.jdbc.JdbcLogRepository;
import io.rocketbase.extension.jdbc.Snowflake;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;

import static java.time.temporal.ChronoUnit.MILLIS;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;


@SuppressWarnings("ConstantConditions")
public abstract class CompatibilityTest {
    public abstract DataSource getDataSource();

    @AfterEach
    public void clearTables() {
        assertTimeoutPreemptively(Duration.ofSeconds(20), () ->
            DbUtils.clearTables(getDataSource())
        );
    }

    @Test
    public void test_jdbc_repository_compatibility_set_data() {
        DataSource dataSource = getDataSource();
        final JdbcLogRepository jdbcLogRepository = new JdbcLogRepository(dataSource, new JavaSerializer(), JdbcLogRepository.DEFAULT_TABLE_NAME, new Snowflake());

        Instant now = Instant.now().truncatedTo(MILLIS);
        final Execution execution = new Execution(now, new TaskInstance("taskName", "213456", new SampleData("sample", 1234L)), true, "pickedBy", now, now, 0, now, 1);
        final ExecutionComplete complete = ExecutionComplete.success(execution, now.minusMillis(10_000), now);


        boolean result = jdbcLogRepository.createIfNotExists(new ExecutionLog(complete));

        assertEquals(result, true);
    }

    public static class SampleData implements Serializable {
        public final String name;
        public final Long value;

        public SampleData(String name, Long value) {
            this.name = name;
            this.value = value;
        }
    }


}
