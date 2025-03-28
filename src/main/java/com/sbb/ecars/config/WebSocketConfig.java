package com.sbb.ecars.config;

import com.sbb.ecars.websocket.WebSocketAudioHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketAudioHandler webSocketAudioHandler;

    public WebSocketConfig(WebSocketAudioHandler webSocketAudioHandler) {
        this.webSocketAudioHandler = webSocketAudioHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketAudioHandler, "/ecars/ws/audio")
                .setAllowedOrigins("*");
    }
}
