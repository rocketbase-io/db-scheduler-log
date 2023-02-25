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
import com.github.kagkarlsson.scheduler.boot.config.DbSchedulerCustomizer;
import com.github.kagkarlsson.scheduler.exceptions.SerializationException;
import com.github.kagkarlsson.scheduler.serializer.Serializer;
import com.github.kagkarlsson.scheduler.stats.StatsRegistry;
import io.rocketbase.extension.LogRepository;
import io.rocketbase.extension.boot.config.DbSchedulerLogProperties;
import io.rocketbase.extension.jdbc.IdProvider;
import io.rocketbase.extension.jdbc.JdbcLogRepository;
import io.rocketbase.extension.jdbc.Snowflake;
import io.rocketbase.extension.stats.LogStatsPlainRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ConfigurableObjectInputStream;

import javax.sql.DataSource;
import java.io.*;
import java.util.Objects;

@Configuration
@EnableConfigurationProperties(DbSchedulerLogProperties.class)
@AutoConfigurationPackage
@AutoConfigureAfter({
    DataSourceAutoConfiguration.class,
})
@AutoConfigureBefore({
    DbSchedulerMetricsAutoConfiguration.class,
})
@ConditionalOnBean(DataSource.class)
@ConditionalOnProperty(value = "db-scheduler-log.enabled", matchIfMissing = true)
public class DbSchedulerLogAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(DbSchedulerLogAutoConfiguration.class);
    private final DbSchedulerLogProperties config;
    private final DataSource existingDataSource;

    public DbSchedulerLogAutoConfiguration(DbSchedulerLogProperties dbSchedulerLogProperties,
                                           DataSource dataSource) {
        this.config = Objects.requireNonNull(dbSchedulerLogProperties, "Can't configure db-scheduler-log without required configuration");
        this.existingDataSource = Objects.requireNonNull(dataSource, "An existing javax.sql.DataSource is required");
    }

    @ConditionalOnMissingBean(LogRepository.class)
    @Bean
    LogRepository logRepository(DbSchedulerCustomizer customizer, IdProvider idProvider) {
        log.debug("Missing LogRepository bean in context, creating a JdbcLogRepository");
        return new JdbcLogRepository(existingDataSource, customizer.serializer().orElse(SPRING_JAVA_SERIALIZER), config.getTableName(), idProvider);
    }

    @ConditionalOnMissingBean(IdProvider.class)
    @Bean
    IdProvider idProvider() {
        log.debug("Missing IdProvider bean in context, creating a Snowflake");
        return new Snowflake();
    }


    @ConditionalOnMissingClass("io.micrometer.core.instrument.MeterRegistry")
    @ConditionalOnMissingBean(StatsRegistry.class)
    @Bean
    StatsRegistry plainLogStatsRegistry(LogRepository logRepository) {
        log.debug("No Spring Boot Actuator / Micrometer has been detected. Will use: {} for StatsRegistry", logRepository.getClass().getName());
        return new LogStatsPlainRegistry(logRepository);
    }

    /**
     * {@link Serializer} compatible with Spring Boot Devtools.
     *
     * @see <a href=
     * "https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#using-boot-devtools-known-restart-limitations">
     * Devtools known limitations</a>
     */
    private static final Serializer SPRING_JAVA_SERIALIZER = new Serializer() {

        public byte[] serialize(Object data) {
            if (data == null)
                return null;
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 ObjectOutput out = new ObjectOutputStream(bos)) {
                out.writeObject(data);
                return bos.toByteArray();
            } catch (Exception e) {
                throw new SerializationException("Failed to serialize object", e);
            }
        }

        public <T> T deserialize(Class<T> clazz, byte[] serializedData) {
            if (serializedData == null)
                return null;
            try (ByteArrayInputStream bis = new ByteArrayInputStream(serializedData);
                 ObjectInput in = new ConfigurableObjectInputStream(bis, Thread.currentThread().getContextClassLoader())) {
                return clazz.cast(in.readObject());
            } catch (Exception e) {
                throw new SerializationException("Failed to deserialize object", e);
            }
        }
    };

}
