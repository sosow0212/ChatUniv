package mju.chatuniv.chat.application;

import mju.chatuniv.chat.application.dto.chat.ChattingHistoryResponse;
import mju.chatuniv.chat.application.dto.gpt.ChatRequest;
import mju.chatuniv.chat.application.dto.gpt.ChatResponse;
import mju.chatuniv.chat.application.dto.gpt.Message;
import mju.chatuniv.chat.domain.chat.Chat;
import mju.chatuniv.chat.domain.chat.ChatRepository;
import mju.chatuniv.chat.domain.chat.Conversation;
import mju.chatuniv.chat.domain.chat.ConversationRepository;
import mju.chatuniv.chat.domain.word.Word;
import mju.chatuniv.chat.domain.word.WordRepository;
import mju.chatuniv.chat.domain.word.Words;
import mju.chatuniv.chat.exception.exceptions.ChattingRoomNotFoundException;
import mju.chatuniv.chat.exception.exceptions.OpenAIErrorException;
import mju.chatuniv.member.domain.Member;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {

    @Value("${api.gpt_prefix_helper}")
    private String PREFIX_HELPER;

    @Value("${api.gpt_prefix_starter}")
    private String PREFIX_STARTER;

    private static final String ENDPOINT = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-3.5-turbo";
    private static final int CHOICE_INDEX = 0;

    @Qualifier("openaiRestTemplate")
    private final RestTemplate restTemplate;
    private final WordRepository wordRepository;
    private final ConversationRepository conversationRepository;
    private final ChatRepository chatRepository;

    public ChatService(final RestTemplate restTemplate,
                       final WordRepository wordRepository,
                       final ConversationRepository conversationRepository,
                       final ChatRepository chatRepository) {
        this.restTemplate = restTemplate;
        this.wordRepository = wordRepository;
        this.conversationRepository = conversationRepository;
        this.chatRepository = chatRepository;
    }

    @Transactional
    public Long createNewChattingRoom(final Member member) {
        Chat chat = chatRepository.save(Chat.createDefault(member));
        return chat.getId();
    }

    @Transactional
    public Conversation useChatBot(final String prompt, final Long chatId) {
        // TODO: 성능 개선 (batch, 도메인)
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChattingRoomNotFoundException(chatId));

        Words pureWordsFromPrompt = Words.fromRawPrompt(prompt);
        Words aleadyBeingWords = Words.ofPureWords(wordRepository.findAllByWords(pureWordsFromPrompt.getWordsToString()));
        aleadyBeingWords.updateFrequencyCount();

        List<Word> newWords = aleadyBeingWords.findNotContainsWordsFromOthers(pureWordsFromPrompt.getWords());
        wordRepository.saveAll(newWords);

        return conversationRepository.save(Conversation.from(prompt, getChatBotRawAnswer(prompt), chat));
    }

    private String getChatBotRawAnswer(final String prompt) {
        ChatRequest promptRequest = getRawAnswer(prompt);
        ChatResponse chatBotAnswer = restTemplate.postForObject(ENDPOINT, promptRequest, ChatResponse.class);

        if (isFailureResponse(chatBotAnswer)) {
            throw new OpenAIErrorException();
        }

        return chatBotAnswer.getChoices()
                .get(CHOICE_INDEX)
                .getMessage()
                .getContent();
    }

    private ChatRequest getRawAnswer(final String prompt) {
        // TODO: 추후 리팩토링 + 순한맛 답변도 추가하기
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system", PREFIX_HELPER));
        messages.add(new Message("system", PREFIX_STARTER));
        messages.add(new Message("user", prompt));

        return new ChatRequest(MODEL, messages);
    }

    private boolean isFailureResponse(final ChatResponse chatBotAnswer) {
        return chatBotAnswer == null || chatBotAnswer.getChoices() == null || chatBotAnswer.getChoices().isEmpty();
    }

    @Transactional(readOnly = true)
    public ChattingHistoryResponse joinChattingRoom(final Long chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChattingRoomNotFoundException(chatId));

        List<Conversation> conversationsHistory = conversationRepository.findAllByChat(chat);
        return ChattingHistoryResponse.from(chat, conversationsHistory);
    }
}
