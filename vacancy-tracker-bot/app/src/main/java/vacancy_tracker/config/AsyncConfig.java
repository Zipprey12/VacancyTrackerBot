package vacancy_tracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync(proxyTargetClass = true)
public class AsyncConfig implements AsyncConfigurer {

    private Executor tasksExecutor;

    @Bean("taskExecutor")
    public Executor taskExecutor() {
        if (tasksExecutor == null) {
            tasksExecutor = createExecutor();
        }
        return tasksExecutor;
    }

    @Override
    public Executor getAsyncExecutor() {
        return taskExecutor();
    }

    private Executor createExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors());
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        return executor;
    }

    @Bean("vacancySearchExecutor")
    public Executor vacancySearchExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("vacancy-search-");
        executor.initialize();
        return executor;
    }
}
