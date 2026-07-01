package vacancy_tracker.services.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.time.LocalTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class StringUtilTest {

    @Nested
    @DisplayName("parseInt")
    class ParseInt {

        static Stream<Arguments> provideValidInt() {
            return Stream.of(
                    Arguments.of("42", 42),
                    Arguments.of("0", 0),
                    Arguments.of("-5", -5),
                    Arguments.of("12 34", 1234),
                    Arguments.of("1 000", 1000),
                    Arguments.of("  123  ", 123),
                    Arguments.of("+7", 7),
                    Arguments.of("2147483647", Integer.MAX_VALUE),
                    Arguments.of("-2147483648", Integer.MIN_VALUE)
            );
        }

        static Stream<String> provideInvalidInt() {
            return Stream.of(
                    "abc",
                    "12.34",
                    "1,234",
                    "1a2b3c",
                    "   ",
                    "2147483648",
                    "-2147483649",
                    "0x123",
                    "null"
            );
        }

        @ParameterizedTest(name = "{index}: input=''{0}''. expected={1}")
        @MethodSource("provideValidInt")
        @DisplayName("Should parse valid integers correctly")
        void validIntegers(String input, int expected) {
            assertThat(StringUtil.parseInt(input)).contains(expected);
        }

        @ParameterizedTest(name = "{index}: input=''{0}''")
        @MethodSource("provideInvalidInt")
        @DisplayName("Should return empty for invalid input")
        void invalidInputs(String input) {
            assertThat(StringUtil.parseInt(input)).isEmpty();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Should return empty for null or blank input")
        void nullAndEmpty(String input) {
            assertThat(StringUtil.parseInt(input)).isEmpty();
        }
    }

    @Nested
    @DisplayName("parseFloat")
    class ParseFloat {

        static Stream<Arguments> provideValidFloat() {
            return Stream.of(
                    Arguments.of("3.5", 3.5f),
                    Arguments.of("3,5", 3.5f),
                    Arguments.of("42", 42.0f),
                    Arguments.of("-7.5", -7.5f),
                    Arguments.of("12 34.5", 1234.5f),
                    Arguments.of("12 34,5", 1234.5f),
                    Arguments.of("0.5", 0.5f),
                    Arguments.of("  .5  ", 0.5f)
            );
        }

        static Stream<String> provideInvalidFloat() {
            return Stream.of(
                    "abc",
                    "12.34.56",
                    "1,2,3",
                    "1a2.3",
                    "   ",
                    ".",
                    ","
            );
        }

        @ParameterizedTest(name = "{index}: input=''{0}'' → expected={1}")
        @MethodSource("provideValidFloat")
        @DisplayName("Should parse valid floats correctly")
        void validFloats(String input, float expected) {
            assertThat(StringUtil.parseFloat(input)).contains(expected);
        }

        @ParameterizedTest(name = "{index}: input=''{0}''")
        @MethodSource("provideInvalidFloat")
        @DisplayName("Should return empty for invalid input")
        void invalidInputs(String input) {
            assertThat(StringUtil.parseFloat(input)).isEmpty();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Should return empty for null or blank input")
        void nullAndEmpty(String input) {
            assertThat(StringUtil.parseFloat(input)).isEmpty();
        }
    }

    @Nested
    @DisplayName("parseBoolean")
    class ParseBoolean {

        static Stream<String> provideTrueVariants() {
            return Stream.of(
                    "true",
                    "TRUE",
                    "True",
                    "  true  ",
                    "  TRUE  ",
                    "  True  ",
                    "true ",
                    " true"
            );
        }

        static Stream<String> provideFalseVariants() {
            return Stream.of(
                    "false",
                    "FALSE",
                    "False",
                    "  false  ",
                    "  FALSE  ",
                    "  False  ",
                    "false ",
                    " false"
            );
        }

        @ParameterizedTest(name = "{index}: input=''{0}'' → should be true")
        @MethodSource("provideTrueVariants")
        @DisplayName("Should parse true variants")
        void trueVariants(String input) {
            assertThat(StringUtil.parseBoolean(input)).contains(true);
        }

        @ParameterizedTest(name = "{index}: input=''{0}'' → should be false")
        @MethodSource("provideFalseVariants")
        @DisplayName("Should parse false variants")
        void falseVariants(String input) {
            assertThat(StringUtil.parseBoolean(input)).contains(false);
        }

        @ParameterizedTest(name = "{index}: input=''{0}''")
        @ValueSource(strings = {"yes", "no", "1", "0", "on", "off", "abc", "truee", "falsse"})
        @DisplayName("Should return empty for unrecognized text")
        void unrecognizedText(String input) {
            assertThat(StringUtil.parseBoolean(input)).isEmpty();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Should return empty for null or blank input")
        void nullAndEmpty(String input) {
            assertThat(StringUtil.parseBoolean(input)).isEmpty();
        }
    }

    @Nested
    @DisplayName("parseTime")
    class ParseTime {

        static Stream<Arguments> provideValidTime() {
            return Stream.of(
                    Arguments.of("14:30", LocalTime.of(14, 30)),
                    Arguments.of("00:00", LocalTime.MIDNIGHT),
                    Arguments.of("23    59   59", LocalTime.of(23, 59, 59)),
                    Arguments.of(" 14:30 ", LocalTime.of(14, 30)),
                    Arguments.of("9 : 05", LocalTime.of(9, 5)),
                    Arguments.of("0:0", LocalTime.MIDNIGHT),
                    Arguments.of("14::30", LocalTime.of(14, 0, 30))
            );
        }

        static Stream<String> provideInvalidTime() {
            return Stream.of(
                    "25:40",
                    "10:60",
                    "24:00",
                    "10:59:60",
                    "14:30:61",
                    "abc",
                    "14:30 AM",
                    "14:30 PM",
                    "   "
            );
        }

        @ParameterizedTest(name = "{index}: input=''{0}'' → expected={1}")
        @MethodSource("provideValidTime")
        @DisplayName("Should parse valid times correctly")
        void validTimes(String input, LocalTime expected) {
            assertThat(StringUtil.parseTime(input)).contains(expected);
        }

        @ParameterizedTest(name = "{index}: input=''{0}''")
        @MethodSource("provideInvalidTime")
        @DisplayName("Should return empty for invalid time")
        void invalidTimes(String input) {
            assertThat(StringUtil.parseTime(input)).isEmpty();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Should return empty for null or blank input")
        void nullAndEmpty(String input) {
            assertThat(StringUtil.parseTime(input)).isEmpty();
        }
    }

    @Nested
    @DisplayName("parseDuration")
    class ParseDuration {

        static Stream<Arguments> provideValidDuration() {
            return Stream.of(
                    Arguments.of("2:30", Duration.ofHours(2).plusMinutes(30)),
                    Arguments.of("0 30", Duration.ofMinutes(30)),
                    Arguments.of("48  :  00", Duration.ofHours(48)),
                    Arguments.of("0:00", Duration.ZERO),
                    Arguments.of(" 2:30 ", Duration.ofHours(2).plusMinutes(30)),
                    Arguments.of(" 2 ", Duration.ofHours(2)),
                    Arguments.of("0:5", Duration.ofMinutes(5)),
                    Arguments.of("1:30:30", Duration.ofHours(1).plusMinutes(30).plusSeconds(30))
            );
        }

        static Stream<String> provideInvalidDuration() {
            return Stream.of(
                    "1:60",
                    "10:60",
                    "1:61",
                    "abc",
                    ":30",
                    "  ",
                    "1.5",
                    "1,5"
            );
        }

        @ParameterizedTest(name = "{index}: input=''{0}'' → expected={1}")
        @MethodSource("provideValidDuration")
        @DisplayName("Should parse valid durations correctly")
        void validDurations(String input, Duration expected) {
            assertThat(StringUtil.parseDuration(input)).contains(expected);
        }

        @ParameterizedTest(name = "{index}: input=''{0}''")
        @MethodSource("provideInvalidDuration")
        @DisplayName("Should return empty for invalid duration")
        void invalidDurations(String input) {
            assertThat(StringUtil.parseDuration(input)).isEmpty();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Should return empty for null or blank input")
        void nullAndEmpty(String input) {
            assertThat(StringUtil.parseDuration(input)).isEmpty();
        }

        @Test
        @DisplayName("Should handle maximum duration")
        void maximumDuration() {
            var result = StringUtil.parseDuration("999999:59");
            assertThat(result)
                    .isPresent()
                    .contains(
                            Duration.ofHours(999999).plusMinutes(59)
                    );
        }
    }
}