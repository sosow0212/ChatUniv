package mju.chatuniv.chat.infrastructure;

import mju.chatuniv.chat.application.dto.gpt.ChatRequest;
import mju.chatuniv.chat.application.dto.gpt.ChatResponse;
import mju.chatuniv.chat.application.dto.gpt.Message;
import mju.chatuniv.chat.exception.exceptions.OpenAIErrorException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class GptChatChatBot implements ChatBot {

    @Value("${api.gpt_prefix_helper}")
    private String PREFIX_HELPER;
    @Value("${api.gpt_prefix_starter}")
    private String PREFIX_STARTER;

    private static final String CHAT_BOT_SYSTEM = "system";
    private static final String USER = "user";
    private static final String ENDPOINT = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-3.5-turbo";
    private static final int CHOICE_INDEX = 0;

    @Qualifier("openaiRestTemplate")
    private final RestTemplate restTemplate;

    public GptChatChatBot(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String getRawAnswer(final String prompt) {
        ChatResponse chatBotAnswer = getChatBotAnswer(prompt);
        return filterAnswer(chatBotAnswer);
    }

    private ChatResponse getChatBotAnswer(final String prompt) {
        ChatRequest promptRequest = preparePrompt(prompt);
        return restTemplate.postForObject(ENDPOINT, promptRequest, ChatResponse.class);
    }

    private ChatRequest preparePrompt(final String prompt) {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message(CHAT_BOT_SYSTEM, PREFIX_HELPER));
        messages.add(new Message(CHAT_BOT_SYSTEM, PREFIX_STARTER));
        messages.add(new Message(USER, prompt));

        return new ChatRequest(MODEL, messages);
    }

    private String filterAnswer(final ChatResponse chatBotAnswer) {
        if (isFailureResponse(chatBotAnswer)) {
            throw new OpenAIErrorException();
        }

        return chatBotAnswer.getChoices()
                .get(CHOICE_INDEX)
                .getMessage()
                .getContent();
    }

    private boolean isFailureResponse(final ChatResponse chatBotAnswer) {
        return chatBotAnswer == null || chatBotAnswer.getChoices() == null || chatBotAnswer.getChoices().isEmpty();
    }

    @Override
    public String getMildAnswer(final String prompt) {
        return null;
    }
}
