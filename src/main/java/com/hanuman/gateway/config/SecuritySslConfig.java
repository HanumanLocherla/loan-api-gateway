package com.hanuman.gateway.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
/*

@Configuration
public class SecuritySslConfig {

    @Value("${banking.api.ssl.keystore-path}")
    private String keyStorePath;

    @Value("${banking.api.ssl.keystore-password}")
    private String keyStorePassword;

    @Value("${banking.api.ssl.truststore-path}")
    private String trustStorePath;

    @Value("${banking.api.ssl.truststore-password}")
    private String trustStorePassword;

    @Bean
    public SslContext bankingSslContext() {
        try {
            // 1. Load Client KeyStore (mTLS Identity)
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            File keyStoreFile = ResourceUtils.getFile(keyStorePath);
            try (InputStream keyStoreStream = new FileInputStream(keyStoreFile)) {
                keyStore.load(keyStoreStream, keyStorePassword.toCharArray());
            }

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

            // 2. Load Bank TrustStore (Server Validation)
            KeyStore trustStore = KeyStore.getInstance("PKCS12");
            File trustStoreFile = ResourceUtils.getFile(trustStorePath);
            try (InputStream trustStoreStream = new FileInputStream(trustStoreFile)) {
                trustStore.load(trustStoreStream, trustStorePassword.toCharArray());
            }

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            // 3. Build Netty-optimized SslContext (Crucial for WebClient performance)
            return SslContextBuilder.forClient()
                    .keyManager(keyManagerFactory)
                    .trustManager(trustManagerFactory)
                    .build();

        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize centralized banking SslContext", e);
        }
    }

}
*/
