package io.rocketbase.extension.compatibility;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.util.DriverDataSource;
import io.rocketbase.extension.DbUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.util.Properties;

@Tag("compatibility")
@Testcontainers
@Disabled
public class Oracle11gCompatibilityTest extends CompatibilityTest {
    @Container
    private static final OracleContainer ORACLE = new OracleContainer("oracleinanutshell/oracle-xe-11g:1.0.0");
    private static HikariDataSource pooledDatasource;

    @BeforeAll
    static void initSchema() {
        final DriverDataSource datasource = new DriverDataSource(ORACLE.getJdbcUrl(), "oracle.jdbc.OracleDriver", new Properties(), ORACLE.getUsername(), ORACLE.getPassword());

        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDataSource(datasource);
        hikariConfig.setMaximumPoolSize(10);
        pooledDatasource = new HikariDataSource(hikariConfig);

        // init schema
        DbUtils.runSqlResource("/oracle_tables.sql").accept(pooledDatasource);
    }

    @BeforeEach
    void overrideSchedulerShutdown() throws InterruptedException {
        Thread.sleep(100);
    }

    @Override
    public DataSource getDataSource() {
        return pooledDatasource;
    }
}
