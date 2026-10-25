package com.technoboost.config;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * Holds the caller's time zone for the current request (from the X-Timezone
 * header). Defaults to UTC. All persistence stays in UTC; this context is
 * only consulted when rendering date-times in responses.
 */
public final class RequestTimezoneContext {

    public static final String TIMEZONE_HEADER = "X-Timezone";

    private static final ThreadLocal<ZoneId> ZONE = ThreadLocal.withInitial(() -> ZoneOffset.UTC);

    private RequestTimezoneContext() {
    }

    public static ZoneId get() {
        return ZONE.get();
    }

    public static void set(ZoneId zoneId) {
        ZONE.set(zoneId);
    }

    public static void clear() {
        ZONE.remove();
    }

    /** Current time in the caller's zone. */
    public static OffsetDateTime now() {
        return OffsetDateTime.now(get());
    }
}
