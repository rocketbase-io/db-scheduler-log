package io.rocketbase.extension.boot;

import com.github.kagkarlsson.scheduler.boot.autoconfigure.DbSchedulerActuatorAutoConfiguration;
import com.github.kagkarlsson.scheduler.boot.autoconfigure.DbSchedulerAutoConfiguration;
import com.github.kagkarlsson.scheduler.boot.autoconfigure.DbSchedulerMetricsAutoConfiguration;
import com.github.kagkarlsson.scheduler.stats.StatsRegistry;
import io.rocketbase.extension.LogRepository;
import io.rocketbase.extension.boot.autoconfigure.DbSchedulerLogAutoConfiguration;
import io.rocketbase.extension.boot.autoconfigure.DbSchedulerLogMetricAutoConfiguration;
import io.rocketbase.extension.jdbc.IdProvider;
import io.rocketbase.extension.stats.LogStatsMicrometerRegistry;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.autoconfigure.health.HealthContributorAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;


public class DbSchedulerLogAutoConfigurationTest {
    private static final Logger log = LoggerFactory.getLogger(DbSchedulerLogAutoConfigurationTest.class);
    private final ApplicationContextRunner ctxRunner;

    public DbSchedulerLogAutoConfigurationTest() {
        ctxRunner = new ApplicationContextRunner()
            .withPropertyValues(
                "spring.application.name=db-scheduler-boot-starter-test",
                "spring.profiles.active=integration-test"
            ).withConfiguration(AutoConfigurations.of(
                DataSourceAutoConfiguration.class,
                SqlInitializationAutoConfiguration.class,
                MetricsAutoConfiguration.class,
                CompositeMeterRegistryAutoConfiguration.class,
                HealthContributorAutoConfiguration.class,
                DbSchedulerMetricsAutoConfiguration.class,
                DbSchedulerActuatorAutoConfiguration.class,
                DbSchedulerAutoConfiguration.class,
                DbSchedulerLogMetricAutoConfiguration.class,
                DbSchedulerLogAutoConfiguration.class
            ));
    }

    @Test
    public void it_should_initialize() {
        ctxRunner.run((AssertableApplicationContext ctx) -> {
            assertThat(ctx).hasSingleBean(DataSource.class);
            assertThat(ctx).hasSingleBean(LogRepository.class);
            assertThat(ctx).hasSingleBean(IdProvider.class);
            assertThat(ctx).hasSingleBean(StatsRegistry.class);

            assertThat(ctx.getBean(StatsRegistry.class)).isInstanceOf(LogStatsMicrometerRegistry.class);
        });
    }
}
