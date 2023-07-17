package mju.chatuniv.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OpenAIRestTemplateConfig {

    private String API_KEY = "";

    @Bean
    @Qualifier("openaiRestTemplate")
    public RestTemplate openaiRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + API_KEY);
            return execution.execute(request, body);
        }));

        return restTemplate;
    }
}
