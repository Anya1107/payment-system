package com.example.filter;

import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.example.util.Constants.TRACE_ID_PARAM;

@Component
public class TraceIdWebFilter implements WebFilter {

    @NotNull
    @Override
    public Mono<Void> filter(@NotNull ServerWebExchange exchange, WebFilterChain chain) {
        String traceId = UUID.randomUUID().toString();

        return chain.filter(exchange)
                .contextWrite(context -> context.put(TRACE_ID_PARAM, traceId))
                .doOnEach(signal -> {
                    if (signal.isOnNext() || signal.isOnError() || signal.isOnComplete()) {
                        MDC.put(TRACE_ID_PARAM, traceId);
                    }
                })
                .doFinally(signal -> MDC.remove(TRACE_ID_PARAM));
    }
}
