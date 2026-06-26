package vacancy_tracker.services.telegram.command.strategy;

import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AsyncExecutionStrategy")
class AsyncExecutionStrategyTest {

    final long chatId = 1L;
    ExecutorService executor;
    AsyncExecutionStrategy strategy;

    @BeforeEach
    void setUp() {
        executor = Executors.newFixedThreadPool(4);
        strategy = new AsyncExecutionStrategy(executor);
    }

    @AfterEach
    void tearDown() {
        executor.shutdownNow();
    }

    @Nested
    @DisplayName("execute(chatId, Runnable)")
    class ExecuteSingle {

        @Test
        @DisplayName("Should run task asynchronously")
        void runsAsynchronously() throws InterruptedException {
            var callingThread = Thread.currentThread();
            var executionThread = new Thread[1];
            CountDownLatch latch = new CountDownLatch(1);

            strategy.execute(chatId, () -> {
                executionThread[0] = Thread.currentThread();
                latch.countDown();
            });

            assertThat(latch.await(1, TimeUnit.SECONDS)).isTrue();
            assertThat(executionThread[0]).isNotEqualTo(callingThread);
        }

        @Test
        @DisplayName("Should not propagate exception, only log it")
        void exceptionDoesNotPropagate() throws InterruptedException {
            CountDownLatch latch = new CountDownLatch(1);

            strategy.execute(chatId, () -> {
                latch.countDown();
                throw new RuntimeException("ошибка");
            });

            assertThat(latch.await(1, TimeUnit.SECONDS)).isTrue();
        }
    }

    @Nested
    @DisplayName("execute(chatId, populate, publish)")
    class ExecutePopulatePublish {

        @Test
        @DisplayName("Should run populate before publish")
        void populateBeforePublish() throws InterruptedException {
            List<String> order = new ArrayList<>();
            CountDownLatch latch = new CountDownLatch(2);

            strategy.execute(chatId,
                    () -> {
                        order.add("populate");
                        latch.countDown();
                    },
                    () -> {
                        order.add("publish");
                        latch.countDown();
                    }
            );

            assertThat(latch.await(1, TimeUnit.SECONDS)).isTrue();
            assertThat(order).containsExactly("populate", "publish");
        }
    }

    @Nested
    @DisplayName("executeWithCheck(chatId, Runnable)")
    class ExecuteWithCheck {

        @Test
        @DisplayName("Should return true on success")
        void returnsTrueOnSuccess() throws Exception {
            var result = strategy.executeWithCheck(chatId, () -> {
            });

            assertThat(result.get(1, TimeUnit.SECONDS)).isTrue();
        }

        @Test
        @DisplayName("Should return false on exception")
        void returnsFalseOnException() throws Exception {
            var result = strategy.executeWithCheck(chatId, () -> {
                throw new RuntimeException();
            });

            assertThat(result.get(1, TimeUnit.SECONDS)).isFalse();
        }
    }

    @Nested
    @DisplayName("independence between calls")
    class Independence {

        @Test
        @DisplayName("Should not block one task while another is running")
        void doesNotBlockBetweenCalls() throws InterruptedException {
            CountDownLatch firstStarted = new CountDownLatch(1);
            CountDownLatch firstCanFinish = new CountDownLatch(1);
            CountDownLatch secondFinished = new CountDownLatch(1);

            strategy.execute(chatId, () -> {
                firstStarted.countDown();
                try {
                    firstCanFinish.await(2, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            assertThat(firstStarted.await(1, TimeUnit.SECONDS)).isTrue();

            strategy.execute(2L, secondFinished::countDown);

            assertThat(secondFinished.await(1, TimeUnit.SECONDS)).isTrue();
            firstCanFinish.countDown();
        }
    }
}