package vacancy_tracker.services.telegram.command.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SyncExecutionStrategyTest {

    final long chatId = 1L;
    SyncExecutionStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new SyncExecutionStrategy();
    }

    @Nested
    @DisplayName("execute(chatId, Runnable)")
    class ExecuteSingle {

        @Test
        @DisplayName("Should run task synchronously in calling thread")
        void runsInCallingThread() {
            var callingThread = Thread.currentThread();
            var executionThread = new Thread[1];

            strategy.execute(chatId, () -> executionThread[0] = Thread.currentThread());

            assertThat(executionThread[0]).isEqualTo(callingThread);
        }
    }

    @Nested
    @DisplayName("execute(chatId, populate, publish)")
    class ExecutePopulatePublish {

        @Test
        @DisplayName("Should run populate before publish")
        void populateBeforePublish() {
            List<String> order = new ArrayList<>();

            strategy.execute(chatId,
                    () -> order.add("populate"),
                    () -> order.add("publish")
            );

            assertThat(order).containsExactly("populate", "publish");
        }
    }

    @Nested
    @DisplayName("executeWithCheck(chatId, Runnable)")
    class ExecuteWithCheck {

        @Test
        @DisplayName("Should return true on success")
        void returnsTrueOnSuccess() {
            var result = strategy.executeWithCheck(chatId, () -> {
            });

            assertThat(result.join()).isTrue();
        }

        @Test
        @DisplayName("Should return false on exception")
        void returnsFalseOnException() {
            var result = strategy.executeWithCheck(chatId, () -> {
                throw new RuntimeException("ошибка");
            });

            assertThat(result.join()).isFalse();
        }
    }
}