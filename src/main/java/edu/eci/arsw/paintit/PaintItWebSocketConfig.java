package edu.eci.arsw.paintit;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.*;


@Configuration
@CrossOrigin(origins = "https://paintitapp.azurewebsites.net", allowCredentials = "true")
@EnableWebSocketMessageBroker
public class PaintItWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stompendpoint")
                .setAllowedOrigins("https://paintitapp.azurewebsites.net")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setPathMatcher(new AntPathMatcher("."));
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

}
