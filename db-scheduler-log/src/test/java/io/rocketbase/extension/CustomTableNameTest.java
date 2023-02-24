package io.rocketbase.extension;

import com.github.kagkarlsson.jdbc.JdbcRunner;
import com.github.kagkarlsson.jdbc.RowMapper;
import com.github.kagkarlsson.scheduler.serializer.JavaSerializer;
import com.github.kagkarlsson.scheduler.task.Execution;
import com.github.kagkarlsson.scheduler.task.ExecutionComplete;
import com.github.kagkarlsson.scheduler.task.TaskInstance;
import io.rocketbase.extension.compatibility.CompatibilityTest;
import io.rocketbase.extension.jdbc.JdbcLogRepository;
import io.rocketbase.extension.jdbc.Snowflake;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.Instant;

import static com.github.kagkarlsson.jdbc.PreparedStatementSetter.NOOP;
import static java.time.temporal.ChronoUnit.MILLIS;

public class CustomTableNameTest {

    private static final String CUSTOM_TABLENAME = "custom_tablename_logs";

    @RegisterExtension
    public EmbeddedPostgresqlExtension DB = new EmbeddedPostgresqlExtension();

    private JdbcLogRepository logRepository;

    @BeforeEach
    public void setUp() {
        logRepository = new JdbcLogRepository(DB.getDataSource(), new JavaSerializer(), CUSTOM_TABLENAME, new Snowflake());

        DbUtils.runSqlResource("postgresql_custom_tablename.sql").accept(DB.getDataSource());
    }

    @Test
    public void can_customize_table_name() {
        Instant now = Instant.now().truncatedTo(MILLIS);
        final Execution execution = new Execution(now, new TaskInstance("taskName", "213456", new CompatibilityTest.SampleData("sample", 1234L)), true, "pickedBy", now, now, 0, now, 1);
        final ExecutionComplete complete = ExecutionComplete.success(execution, now.minusMillis(10_000), now);


        logRepository.createIfNotExists(new ExecutionLog(complete));

        JdbcRunner jdbcRunner = new JdbcRunner(DB.getDataSource());
        jdbcRunner.query("SELECT count(1) AS number_of_tasks FROM " + CUSTOM_TABLENAME, NOOP, (RowMapper<Integer>) rs -> rs.getInt("number_of_tasks"));

    }

    @AfterEach
    public void tearDown() {
        new JdbcRunner(DB.getDataSource()).execute("DROP TABLE " + CUSTOM_TABLENAME, NOOP);
    }

}
