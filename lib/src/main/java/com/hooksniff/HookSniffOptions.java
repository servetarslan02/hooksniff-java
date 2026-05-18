package com.hooksniff;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Setter
@Getter
public final class HookSniffOptions {
    public static final String DEFAULT_URL = "https://hooksniff-api-1046140057667.europe-west1.run.app";

    private String serverUrl;
    private final List<Long> retrySchedule = Arrays.asList(1000L, 2000L, 4000L);
    private boolean debug = false;
}
