package com.hanuman.gateway.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    // 1. Define the Builder bean that Spring Boot says is missing
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient reactiveWebClient(WebClient.Builder webClientBuilder) {

        // 1. Configure Connection Pooling (Elastic pool optimized for banking traffic)
        ConnectionProvider connectionProvider = ConnectionProvider.builder("banking-pool")
                .maxConnectionPools(200) // Total parallel connections allowed
                .pendingAcquireTimeout(Duration.ofSeconds(3)) // Max time to wait for a free connection from the pool
                .maxIdleTime(Duration.ofSeconds(20))     // Max idle time before dropping connection
                .build();

        // 2. // 2. Configure the Underlying Netty HTTP Client with Centralized SSL Context
        HttpClient httpClient = HttpClient.create(connectionProvider)
                // .secure(sslContextSpec -> sslContextSpec.sslContext(bankingSslContext))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000) // 2-second connection timeout
                .responseTimeout(Duration.ofSeconds(5))            // 5-second complete response timeout
                .doOnConnected(connection -> connection
                        .addHandlerLast(new ReadTimeoutHandler(5, TimeUnit.SECONDS))  // Packet read timeout
                        .addHandlerLast(new WriteTimeoutHandler(5, TimeUnit.SECONDS))); // Packet write timeout

        // 3. Assemble and return the isolated WebClient instance
        return webClientBuilder
                //.baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
