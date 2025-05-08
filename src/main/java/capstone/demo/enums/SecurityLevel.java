package capstone.demo.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SecurityLevel {
    GENERAL(1,"일반"),
    RESTRICTED(2,"제한"),
    IMPORTANT(3,"중요"),
    CONFIDENTIAL(4,"기밀");

    private final int level;
    private final String description;
}
