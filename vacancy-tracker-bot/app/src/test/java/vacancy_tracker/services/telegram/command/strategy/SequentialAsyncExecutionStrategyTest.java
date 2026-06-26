package vacancy_tracker.services.telegram.command.strategy;

import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SequentialAsyncExecutionStrategy")
class SequentialAsyncExecutionStrategyTest {

    ExecutorService executor;
    SequentialAsyncExecutionStrategy strategy;

    @BeforeEach
    void setUp() {
        executor = Executors.newFixedThreadPool(4);
        strategy = new SequentialAsyncExecutionStrategy(executor);
    }

    @AfterEach
    void tearDown() {
        executor.shutdownNow();
    }

    @Test
    @DisplayName("Should remove completed chains from internal map")
    void cleansUpAfterCompletion() throws InterruptedException {
        var chatId = 99;
        CountDownLatch latch = new CountDownLatch(1);

        strategy.execute(chatId, latch::countDown);

        assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
        Thread.sleep(200);

        CountDownLatch secondLatch = new CountDownLatch(1);
        strategy.execute(chatId, secondLatch::countDown);

        assertThat(secondLatch.await(5, TimeUnit.SECONDS)).isTrue();
    }

    @Nested
    @DisplayName("execute(chatId, Runnable)")
    class ExecuteSingle {

        @Test
        @DisplayName("Should run tasks for same chatId strictly in order")
        void sameChatTasksAreOrdered() throws InterruptedException {
            var chatId = 1;
            int taskCount = 20;
            var order = Collections.synchronizedList(new ArrayList<>());
            CountDownLatch latch = new CountDownLatch(taskCount);

            for (int i = 0; i < taskCount; i++) {
                int taskNum = i;
                strategy.execute(chatId, () -> {
                    order.add(taskNum);
                    latch.countDown();
                });
            }

            assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
            assertThat(order).containsExactlyElementsOf(
                    IntStream.range(0, taskCount).boxed().toList()
            );
        }

        @Test
        @DisplayName("Should run tasks for different chatIds independently")
        void differentChatsAreIndependent() throws InterruptedException {
            int taskCount = 5;
            CountDownLatch latch = new CountDownLatch(10);
            var chat1Count = new AtomicInteger();
            var chat2Count = new AtomicInteger();

            for (int i = 0; i < taskCount; i++) {
                strategy.execute(1L, () -> {
                    chat1Count.incrementAndGet();
                    latch.countDown();
                });
                strategy.execute(2L, () -> {
                    chat2Count.incrementAndGet();
                    latch.countDown();
                });
            }

            assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
            assertThat(chat1Count.get()).isEqualTo(taskCount);
            assertThat(chat2Count.get()).isEqualTo(taskCount);
        }

        @Test
        @DisplayName("Should not break chain when a task throws")
        void exceptionDoesNotBreakChain() throws InterruptedException {
            var chatId = 1;
            CountDownLatch latch = new CountDownLatch(1);
            var successCount = new AtomicInteger();

            strategy.execute(chatId, () -> {
                throw new RuntimeException("ошибка");
            });
            strategy.execute(chatId, () -> {
                successCount.incrementAndGet();
                latch.countDown();
            });

            assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
            assertThat(successCount.get()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("execute(chatId, populate, publish)")
    class ExecutePopulatePublish {

        @Test
        @DisplayName("Should run populate before publish")
        void populateBeforePublish() throws InterruptedException {
            var chatId = 5;
            var order = Collections.synchronizedList(new ArrayList<>());
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

            assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
            assertThat(order).containsExactly("populate", "publish");
        }

        @Test
        @DisplayName("Should keep multiple populate/publish pairs in strict order for same chatId")
        void multiplePairsAreOrdered() throws InterruptedException {
            long chatId = 1L;
            var order = Collections.synchronizedList(new ArrayList<>());
            CountDownLatch latch = new CountDownLatch(6);

            for (int i = 0; i < 3; i++) {
                int idx = i;
                strategy.execute(chatId,
                        () -> {
                            order.add("populate-" + idx);
                            latch.countDown();
                        },
                        () -> {
                            order.add("publish-" + idx);
                            latch.countDown();
                        }
                );
            }

            assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
            assertThat(order).containsExactly(
                    "populate-0", "publish-0",
                    "populate-1", "publish-1",
                    "populate-2", "publish-2"
            );
        }
    }

    @Nested
    @DisplayName("executeWithCheck(chatId, Runnable)")
    class ExecuteWithCheck {

        @Test
        @DisplayName("Should return true on success")
        void returnsTrueOnSuccess() throws Exception {
            var result = strategy.executeWithCheck(10L, () -> {
            });

            assertThat(result.get(1, TimeUnit.SECONDS)).isTrue();
        }

        @Test
        @DisplayName("Should return false on exception")
        void returnsFalseOnException() throws Exception {
            var result = strategy.executeWithCheck(1, () -> {
                throw new RuntimeException("ошибка");
            });

            assertThat(result.get(1, TimeUnit.SECONDS)).isFalse();
        }

        @Test
        @DisplayName("Should run after previously queued tasks for same chatId")
        void isSequentialWithPreviousTasks() throws Exception {
            var chatId = 1;
            var order = Collections.synchronizedList(new ArrayList<>());
            CountDownLatch firstStarted = new CountDownLatch(1);
            CountDownLatch firstCanFinish = new CountDownLatch(1);

            strategy.execute(chatId, () -> {
                firstStarted.countDown();
                try {
                    firstCanFinish.await(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                order.add("first");
            });

            assertThat(firstStarted.await(5, TimeUnit.SECONDS)).isTrue();

            var future = strategy.executeWithCheck(chatId, () -> order.add("second"));

            assertThat(order).doesNotContain("second");

            firstCanFinish.countDown();
            assertThat(future.get(5, TimeUnit.SECONDS)).isTrue();
            assertThat(order).containsExactly("first", "second");
        }
    }
}