package mju.chatuniv.chat.application;

import mju.chatuniv.chat.application.dto.chat.ChattingHistoryResponse;
import mju.chatuniv.chat.application.dto.chat.ConversationResponse;
import mju.chatuniv.chat.application.dto.gpt.ChatRequest;
import mju.chatuniv.chat.application.dto.gpt.ChatResponse;
import mju.chatuniv.chat.domain.chat.Chat;
import mju.chatuniv.chat.domain.chat.ChatRepository;
import mju.chatuniv.chat.domain.chat.Conversation;
import mju.chatuniv.chat.domain.chat.ConversationRepository;
import mju.chatuniv.chat.domain.word.Word;
import mju.chatuniv.chat.domain.word.WordRepository;
import mju.chatuniv.chat.domain.word.Words;
import mju.chatuniv.member.domain.Member;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ChatService {

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
    public Long makeChattingRoom(final Member member) {
        Chat chat = chatRepository.save(Chat.createDefault(member));
        return chat.getId();
    }

    @Transactional
    public ConversationResponse useChatBot(final String prompt, final Long chatId) {
        // TODO: 개선 필요

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 번호의 채팅방이 없습니다."));

        // 1. 퓨어한 단어로 만든다.
        Words inputWords = Words.fromRawPrompt(prompt);

        // 2. DB에서 이미 있는 단어라면 update(모든 채팅에서 몇 번 나왔는지에 대한 count += 1)
        Words wordsInDb = Words.ofPureWords(wordRepository.findAllByWords(inputWords.getWordsToString()));
        wordsInDb.updateFrequencyCount();

        // 3. 새로운 단어라면 insert (+ 1)
        List<Word> wordsNotInDb = wordsInDb.findNotContainsWordsFromOthers(inputWords.getWords());
        wordRepository.saveAll(wordsNotInDb);

        // 4. Conversation 저장 및 답변 받기
        String chatBotAnswer = getChatBotAnswer(prompt);
        Conversation conversation = conversationRepository.save(Conversation.from(prompt, chatBotAnswer, chat));
        return ConversationResponse.from(conversation);
    }

    private String getChatBotAnswer(final String prompt) {
        ChatRequest request = new ChatRequest(MODEL, prompt);
        ChatResponse response = restTemplate.postForObject(ENDPOINT, request, ChatResponse.class);

        if (isFailureResponse(response)) {
            return "OpenAI의 API의 문제로 진행할 수 없습니다.";
        }

        return response.getChoices()
                .get(CHOICE_INDEX)
                .getMessage()
                .getContent();
    }

    private boolean isFailureResponse(final ChatResponse response) {
        return response == null
                || response.getChoices() == null
                || response.getChoices().isEmpty();
    }

    public ChattingHistoryResponse joinChattingRoom(final Long chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(IllegalArgumentException::new);

        List<Conversation> conversations = conversationRepository.findAllByChat(chat);
        return ChattingHistoryResponse.from(chat, conversations);
    }
}
