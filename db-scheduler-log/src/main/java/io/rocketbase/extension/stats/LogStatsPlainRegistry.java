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
package io.rocketbase.extension.stats;

import com.github.kagkarlsson.scheduler.stats.StatsRegistry;
import com.github.kagkarlsson.scheduler.task.ExecutionComplete;
import io.rocketbase.extension.ExecutionLog;
import io.rocketbase.extension.LogRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LogStatsPlainRegistry implements StatsRegistry {

    private final LogRepository logRepository;
    private ExecutorService executorService;

    public LogStatsPlainRegistry(LogRepository logRepository) {
        this.logRepository = logRepository;
        this.executorService = Executors.newFixedThreadPool(5);
    }

    @Override
    public void register(SchedulerStatsEvent e) {
    }

    @Override
    public void register(CandidateStatsEvent e) {
    }

    @Override
    public void register(ExecutionStatsEvent e) {
    }

    @Override
    public void registerSingleCompletedExecution(ExecutionComplete completeEvent) {
        executorService.submit(() -> {
            logRepository.createIfNotExists(new ExecutionLog(completeEvent));
        });
    }
}
