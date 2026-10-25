package com.technoboost.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RequestTimezoneFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        ZoneId zone = ZoneOffset.UTC;
        String header = request.getHeader(RequestTimezoneContext.TIMEZONE_HEADER);
        if (header != null && !header.isBlank()) {
            try {
                zone = ZoneId.of(header.trim());
            } catch (DateTimeException e) {
                log.debug("Ignoring invalid {} header: {}", RequestTimezoneContext.TIMEZONE_HEADER, header);
            }
        }

        RequestTimezoneContext.set(zone);
        try {
            filterChain.doFilter(request, response);
        } finally {
            RequestTimezoneContext.clear();
        }
    }
}
