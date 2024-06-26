package com.example.jolvre.chat.config;

import com.example.jolvre.chat.handler.StompHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final StompHandler stompHandler;
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // socketJs 클라이언트가 WebSocket 핸드셰이크를 하기 위해 연결할 endpoint를 지정할 수 있다.
        registry.addEndpoint("/ws/chat")
                .setAllowedOrigins("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        System.out.println("configureMessageBroker 실행");
        registry.setApplicationDestinationPrefixes("/pub") // 클라이언트가 구독하는 경로
                .enableSimpleBroker("/sub"); // 메시지 브로커의 Prefix
    } 



    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // CONNECT, DISCONNECT, CONNECTING 형태의 메세지 요청이 들어오면 인바운드 채널을 거친다.
        registration.interceptors(stompHandler);
    }
}
