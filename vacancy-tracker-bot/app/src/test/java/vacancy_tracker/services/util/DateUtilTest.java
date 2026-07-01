package vacancy_tracker.services.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DateUtil")
class DateUtilTest {

    @Nested
    @DisplayName("nextDayOfWeek")
    class NextDayOfWeek {

        static Stream<Arguments> provideNextDayOfWeekScenarios() {
            return Stream.of(
                    //Тот же день, время еще не прошло
                    Arguments.of(
                            LocalDateTime.of(2026, 6, 1, 10, 0),
                            DayOfWeek.MONDAY.getValue(),
                            LocalTime.of(12, 0),
                            LocalDateTime.of(2026, 6, 1, 12, 0)
                    ),

                    //Тот же день, время уже прошло
                    Arguments.of(
                            LocalDateTime.of(2026, 6, 1, 14, 0),
                            DayOfWeek.MONDAY.getValue(),
                            LocalTime.of(12, 0),
                            LocalDateTime.of(2026, 6, 8, 12, 0)
                    ),

                    //Будущий день на этой неделе
                    Arguments.of(
                            LocalDateTime.of(2026, 6, 1, 10, 0),
                            DayOfWeek.FRIDAY.getValue(),
                            LocalTime.of(9, 0),
                            LocalDateTime.of(2026, 6, 5, 9, 0)
                    ),

                    //Прошедший день на этой неделе
                    Arguments.of(
                            LocalDateTime.of(2026, 6, 5, 10, 0),
                            DayOfWeek.MONDAY.getValue(),
                            LocalTime.of(9, 0),
                            LocalDateTime.of(2026, 6, 8, 9, 0)
                    ),

                    //Тот же день, то же время
                    Arguments.of(
                            LocalDateTime.of(2026, 6, 1, 12, 0),
                            DayOfWeek.MONDAY.getValue(),
                            LocalTime.of(12, 0),
                            LocalDateTime.of(2026, 6, 1, 12, 0)
                    )
            );
        }

        @ParameterizedTest(name = "{index}: from={0}, dayOfWeek={1}, targetTime={2}. expected={3}")
        @MethodSource("provideNextDayOfWeekScenarios")
        @DisplayName("Should calculate next day of week correctly")
        void testNextDayOfWeek(
                LocalDateTime from,
                int dayOfWeek,
                LocalTime targetTime,
                LocalDateTime expected) {

            var result = DateUtil.nextDayOfWeek(from, dayOfWeek, targetTime);

            assertThat(result)
                    .as("Result should match expected date-time")
                    .isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("toUnixSeconds / fromUnixSeconds")
    class UnixConversion {

        @Test
        @DisplayName("Should return 0 for null input")
        void nullReturnsZero() {
            assertThat(DateUtil.toUnixSeconds(null)).isZero();
        }

        @Test
        @DisplayName("Should maintain accuracy when translating")
        void roundTrip() {
            var original = LocalDateTime.of(2026, 6, 1, 12, 5, 2);
            var unix = DateUtil.toUnixSeconds(original);
            var restored = DateUtil.fromUnixSeconds(unix);

            assertThat(restored).isEqualTo(original);
        }
    }

    @Nested
    @DisplayName("nextTimeWithInterval")
    class NextTimeWithInterval {

        static Stream<Arguments> futureResultCases() {
            return Stream.of(
                    Arguments.of(5, Duration.ofDays(1)),
                    Arguments.of(30, Duration.ofMinutes(5))
            );
        }

        @ParameterizedTest(name = "from {0} days ago, interval={1}")
        @DisplayName("Should return a result in the future")
        @MethodSource("futureResultCases")
        void resultIsInFuture(int daysAgo, Duration interval) {
            var now = LocalDateTime.now();
            var from = now.minusDays(daysAgo);
            var newTime = LocalTime.of(9, 30);

            var result = DateUtil.nextTimeWithInterval(from, newTime, interval);

            assertThat(result).isAfterOrEqualTo(now);
        }

        @Test
        @DisplayName("Should preserve exact time when interval is a multiple of a day")
        void preservesTimeWhenIntervalIsWholeDays() {
            var from = LocalDateTime.now().minusDays(5);
            var newTime = LocalTime.of(9, 30);
            var interval = Duration.ofDays(1);

            var result = DateUtil.nextTimeWithInterval(from, newTime, interval);

            assertThat(result.toLocalTime()).isEqualTo(newTime);
        }

        @Test
        @DisplayName("Should not shift the date when candidate is already in the future")
        void candidateInFutureKeepsSameDate() {
            var from = LocalDateTime.now().plusDays(1);
            var newTime = LocalTime.of(12, 0);
            var interval = Duration.ofDays(1);

            var result = DateUtil.nextTimeWithInterval(from, newTime, interval);

            assertThat(result.toLocalDate()).isEqualTo(from.toLocalDate());
        }
    }
}