package com.est.back.match;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.*;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.*;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/queue", "/topic", "/user");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 알림용 엔드포인트
        registry.addEndpoint("/ws/notifications")
                .addInterceptors(new CustomUserIdInterceptor())
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    @Override
                    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                        return () -> (String) attributes.getOrDefault("userId", UUID.randomUUID().toString());
                    }
                })
                .withSockJS();

        // 채팅용 엔드포인트
        registry.addEndpoint("/ws/matchchat")
                .addInterceptors(new CustomUserIdInterceptor())
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    @Override
                    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                        return () -> (String) attributes.getOrDefault("userId", UUID.randomUUID().toString());
                    }
                })
                .withSockJS();
    }
}
