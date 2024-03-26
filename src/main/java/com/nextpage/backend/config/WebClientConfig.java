package com.nextpage.backend.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create().tcpConfiguration(
                        tcpClient -> tcpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                                .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(5000)))
                                .doOnConnected(conn -> conn.addHandlerLast(new WriteTimeoutHandler(5000)))
                                .option(ChannelOption.SO_KEEPALIVE, true)
                )))
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024 * 10)) // 10MB 버퍼 사이즈로 설정
                        .build());
    }
}
