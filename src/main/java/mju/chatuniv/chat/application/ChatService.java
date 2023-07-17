package mju.chatuniv.chat.application;

import mju.chatuniv.chat.application.dto.ChatRequest;
import mju.chatuniv.chat.application.dto.ChatResponse;
import mju.chatuniv.chat.domain.ChatRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private static final String ENDPOINT = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-3.5-turbo";

    @Qualifier("openaiRestTemplate")
    private final RestTemplate restTemplate;
    private final ChatRepository chatRepository;

    public ChatService(final ChatRepository chatRepository, final RestTemplate restTemplate) {
        this.chatRepository = chatRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public String generateText(final String prompt) {
        List<String> splitUserPrompt = Arrays.stream(prompt.split(" ")).collect(Collectors.toList());

        String gptAnswer = getAnswer(prompt);
        return gptAnswer;
    }

    private String getAnswer(final String prompt) {
        ChatRequest request = new ChatRequest(MODEL, prompt);
        ChatResponse response = restTemplate.postForObject(ENDPOINT, request, ChatResponse.class);

        if (isFailureResponse(response)) {
            return "OpenAI API의 문제로 진행할 수 없습니다.";
        }

        return response.getChoices().get(0).getMessage().getContent();
    }

    private boolean isFailureResponse(final ChatResponse response) {
        return response == null || response.getChoices() == null || response.getChoices().isEmpty();
    }
}
