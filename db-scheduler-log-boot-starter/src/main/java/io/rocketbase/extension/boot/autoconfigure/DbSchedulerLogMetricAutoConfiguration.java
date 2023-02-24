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
package io.rocketbase.extension.boot.autoconfigure;

import com.github.kagkarlsson.scheduler.boot.autoconfigure.DbSchedulerMetricsAutoConfiguration;
import com.github.kagkarlsson.scheduler.stats.StatsRegistry;
import com.github.kagkarlsson.scheduler.task.Task;
import io.micrometer.core.instrument.MeterRegistry;
import io.rocketbase.extension.LogRepository;
import io.rocketbase.extension.stats.LogStatsMicrometerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConditionalOnClass({
    MetricsAutoConfiguration.class,
    CompositeMeterRegistryAutoConfiguration.class,
})
@AutoConfigureAfter({
    MetricsAutoConfiguration.class,
    CompositeMeterRegistryAutoConfiguration.class,
})
@AutoConfigureBefore(DbSchedulerMetricsAutoConfiguration.class)
@ConditionalOnProperty(value = "db-scheduler-log.enabled", matchIfMissing = true)
public class DbSchedulerLogMetricAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(DbSchedulerLogMetricAutoConfiguration.class);
    private final List<Task<?>> configuredTasks;

    public DbSchedulerLogMetricAutoConfiguration(List<Task<?>> configuredTasks) {
        this.configuredTasks = configuredTasks;
    }

    @ConditionalOnClass(MeterRegistry.class)
    @ConditionalOnBean(MeterRegistry.class)
    @ConditionalOnMissingBean(StatsRegistry.class)
    @Bean
    StatsRegistry micrometerLogStatsRegistry(MeterRegistry registry, LogRepository logRepository) {
        log.debug("Spring Boot Actuator and Micrometer detected. Will use: {} for StatsRegistry", registry.getClass().getName());
        return new LogStatsMicrometerRegistry(registry, configuredTasks, logRepository);
    }
}

