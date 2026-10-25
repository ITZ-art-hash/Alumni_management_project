package com.technoboost.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.StdSerializer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Renders all response date-times in the caller's time zone (X-Timezone
 * header, default UTC) as ISO-8601 with offset, e.g. 2026-06-11T10:30:00+05:30.
 * Stored values are always UTC; this is a presentation-layer conversion only.
 */
@Configuration
public class JacksonTimezoneConfig {

    @Bean
    public JsonMapperBuilderCustomizer requestTimezoneCustomizer() {
        return builder -> builder.addModule(new SimpleModule("request-timezone")
                .addSerializer(LocalDateTime.class, new UtcLocalDateTimeSerializer())
                .addSerializer(Instant.class, new InstantSerializer()));
    }

    /** LocalDateTime values are persisted as UTC wall-clock time. */
    static final class UtcLocalDateTimeSerializer extends StdSerializer<LocalDateTime> {

        UtcLocalDateTimeSerializer() {
            super(LocalDateTime.class);
        }

        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializationContext ctxt) {
            OffsetDateTime inUserZone = value.atOffset(ZoneOffset.UTC)
                    .atZoneSameInstant(RequestTimezoneContext.get())
                    .toOffsetDateTime();
            gen.writeString(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(inUserZone));
        }
    }

    static final class InstantSerializer extends StdSerializer<Instant> {

        InstantSerializer() {
            super(Instant.class);
        }

        @Override
        public void serialize(Instant value, JsonGenerator gen, SerializationContext ctxt) {
            OffsetDateTime inUserZone = value.atZone(RequestTimezoneContext.get()).toOffsetDateTime();
            gen.writeString(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(inUserZone));
        }
    }
}
