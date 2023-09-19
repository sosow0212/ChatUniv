package mju.chatuniv.chat.infrastructure;

import mju.chatuniv.chat.service.dto.gpt.ChatRequest;
import mju.chatuniv.chat.service.dto.gpt.ChatResponse;
import mju.chatuniv.chat.service.dto.gpt.Message;
import mju.chatuniv.chat.exception.exceptions.OpenAIErrorException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class GptChatChatBot implements ChatBot {

    @Value("${api.gpt_raw_prefix_helper}")
    private String rawPrefixHelper;
    @Value("${api.gpt_raw_prefix_starter}")
    private String rawPrefixStarter;
    @Value("${api.gpt_mild_prefix_helper}")
    private String mildPrefixHelper;
    @Value("${api.gpt_mild_prefix_starter}")
    private String mildPrefixStarter;

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
        return getChatBotAnswer(prompt, rawPrefixHelper, rawPrefixStarter);
    }

    @Override
    public String getMildAnswer(final String prompt) {
        return getChatBotAnswer(prompt, mildPrefixHelper, mildPrefixStarter);
    }

    private String getChatBotAnswer(final String prompt, String prefixHelper, String prefixStarter) {
        ChatResponse chatBotAnswer = getChatBotResponse(prompt, prefixHelper, prefixStarter);
        return filterAnswer(chatBotAnswer);
    }

    private ChatResponse getChatBotResponse(final String prompt, String prefixHelper, String prefixStarter) {
        ChatRequest promptRequest = preparePrompt(prompt, prefixHelper, prefixStarter);
        return restTemplate.postForObject(ENDPOINT, promptRequest, ChatResponse.class);
    }

    private ChatRequest preparePrompt(final String prompt, String prefixHelper, String prefixStarter) {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message(CHAT_BOT_SYSTEM, prefixHelper));
        messages.add(new Message(CHAT_BOT_SYSTEM, prefixStarter));
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
}
