package edu.eci.arsw.paintit;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocketMessageBroker
@EnableScheduling
public class PaintItWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stompendpoint")
                .setAllowedOrigins("https://paintitapp.azurewebsites.net",
                        "https://paintitfront.azurewebsites.net",
                        "https://paintitgateway.eastus.cloudapp.azure.com",
                        "http://paintitapp.azurewebsites.net",
                        "http://paintitfront.azurewebsites.net",
                        "http://paintitgateway.eastus.cloudapp.azure.com")
                .withSockJS();
    }

}
