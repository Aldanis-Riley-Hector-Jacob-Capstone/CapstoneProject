package com.healthpointsfitness.healthpointsfitness.configuration.WebSockets;

import com.healthpointsfitness.healthpointsfitness.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


//@Configuration
@EnableWebSocket
@Configuration
public class VigoChatConfiguration implements WebSocketConfigurer {
    @Autowired
    UserRepository userRepository;

    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        try {
            registry.addHandler(new VigoChatHandler(userRepository), "/freechat").setAllowedOrigins("*");
        }catch(ClassCastException e){
            e.printStackTrace();
        }
    }
}




