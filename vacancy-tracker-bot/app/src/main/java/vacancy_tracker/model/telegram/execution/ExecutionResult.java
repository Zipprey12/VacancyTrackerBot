package vacancy_tracker.model.telegram.execution;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ExecutionResult {

    private final boolean isSuccess;
    private final ExecutionFailReason failReason;

    public static ExecutionResult success() {
        return new ExecutionResult(true, null);
    }

    public static ExecutionResult fail(ExecutionFailReason reason) {
        return new ExecutionResult(false, reason);
    }
}
