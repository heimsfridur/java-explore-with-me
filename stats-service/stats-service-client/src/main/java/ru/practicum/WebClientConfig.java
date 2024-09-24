package ru.practicum;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

//@Configuration
public class WebClientConfig {
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
