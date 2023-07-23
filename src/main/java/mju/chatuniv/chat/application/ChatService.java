package mju.chatuniv.chat.application;

import mju.chatuniv.chat.application.dto.chat.ChattingHistoryResponse;
import mju.chatuniv.chat.application.dto.gpt.ChatRequest;
import mju.chatuniv.chat.application.dto.gpt.ChatResponse;
import mju.chatuniv.chat.domain.chat.Chat;
import mju.chatuniv.chat.domain.chat.ChatRepository;
import mju.chatuniv.chat.domain.chat.Conversation;
import mju.chatuniv.chat.domain.chat.ConversationRepository;
import mju.chatuniv.chat.domain.word.Word;
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
    private final ChatRepository chatRepository;
    private final ConversationRepository conversationRepository;

    public ChatService(final ChatRepository chatRepository,
                       final RestTemplate restTemplate,
                       final ConversationRepository conversationRepository) {
        this.chatRepository = chatRepository;
        this.restTemplate = restTemplate;
        this.conversationRepository = conversationRepository;
    }

    @Transactional
    public Long makeChattingRoom(final Member member) {
        Chat chat = chatRepository.save(Chat.createDefault(member));
        return chat.getId();
    }

    @Transactional
    public String useChatBot(final String prompt, final String chatId) {
        // 1. 퓨어한 단어로 만든다.
        // 2. DB에서 이미 있는 단어라면 update(모든 채팅에서 몇 번 나왔는지에 대한 count += 1)
        // 3. 새로운 단어라면 insert (+ 1)
        // 4. Conversation 저장해주기
        // 5. 답변 받아서 사용자한테 주기 (이 때, gpt 요청 DTO에 이전 질문들 넣어주기)

        // 1. JPQL N+1
        // 2. 멀티 스레드 병렬화해서 로직 속도 증가시키기
        // 3. 질문 내용을 split해서 두 번에 걸쳐서 보내주는 방법 or 질문을 영어로 바꾸고 전송해주기

        Words words = Words.from(prompt);
        List<Word> pureWords = words.getWords();


        return getChatBotAnswer(prompt);
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
        //
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(IllegalArgumentException::new);

        List<Conversation> conversations = conversationRepository.findAllByChat(chat);
        return ChattingHistoryResponse.from(chat, conversations);
    }
}
