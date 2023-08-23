package mju.chatuniv.global.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OpenAIRestTemplateConfig {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER_TOKEN = "Bearer ";

    @Value("${api.gpt_key}")
    private String API_KEY;

    @Bean
    @Qualifier("openaiRestTemplate")
    public RestTemplate openaiRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getInterceptors().add(((request, body, execution) -> {
            request.getHeaders()
                    .add(AUTHORIZATION, BEARER_TOKEN + API_KEY);
            return execution.execute(request, body);
        }));

        return restTemplate;
    }
}
