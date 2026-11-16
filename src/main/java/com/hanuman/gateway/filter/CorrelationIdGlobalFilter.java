package com.hanuman.gateway.filter;

import java.util.UUID;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CorrelationIdGlobalFilter implements GlobalFilter, Ordered {

    public static final String HEADER = "X-Correlation-ID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
            GatewayFilterChain chain) {

        String incoming = exchange.getRequest()
                .getHeaders()
                .getFirst(HEADER);

        String correlationId =
                (incoming == null || incoming.isBlank())
                        ? UUID.randomUUID().toString()
                        : incoming;

        // Add correlation ID to the mutated request
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(request -> request.headers(headers ->
                        headers.set(HEADER, correlationId)))
                .build();

        // Add correlation ID to the response
        ServerHttpResponse response = mutatedExchange.getResponse();

        response.beforeCommit(() -> {
            response.getHeaders().set(HEADER, correlationId);
            return Mono.empty();
        });

        return chain.filter(mutatedExchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
