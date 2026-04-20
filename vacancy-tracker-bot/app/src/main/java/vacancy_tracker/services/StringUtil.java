package vacancy_tracker.services;

import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class StringUtil {

    public static Optional<Integer> parseInt(String text) {
        if(text == null){
            return Optional.empty();
        }
        try {
            return Optional.of(Integer.parseInt(text));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
