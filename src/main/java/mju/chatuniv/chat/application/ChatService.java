package mju.chatuniv.chat.application;

import mju.chatuniv.chat.application.dto.chat.ChattingHistoryResponse;
import mju.chatuniv.chat.domain.chat.Chat;
import mju.chatuniv.chat.domain.chat.ChatRepository;
import mju.chatuniv.chat.domain.chat.Conversation;
import mju.chatuniv.chat.domain.chat.ConversationRepository;
import mju.chatuniv.chat.domain.word.Word;
import mju.chatuniv.chat.domain.word.WordRepository;
import mju.chatuniv.chat.domain.word.Words;
import mju.chatuniv.chat.exception.exceptions.ChattingRoomNotFoundException;
import mju.chatuniv.chat.infrastructure.ChatBot;
import mju.chatuniv.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatService {

    private final WordRepository wordRepository;
    private final ConversationRepository conversationRepository;
    private final ChatRepository chatRepository;
    private final ChatBot chatBot;

    public ChatService(final WordRepository wordRepository,
                       final ConversationRepository conversationRepository,
                       final ChatRepository chatRepository,
                       final ChatBot chatBot) {
        this.wordRepository = wordRepository;
        this.conversationRepository = conversationRepository;
        this.chatRepository = chatRepository;
        this.chatBot = chatBot;
    }

    @Transactional
    public Long createNewChattingRoom(final Member member) {
        Chat chat = chatRepository.save(Chat.createDefault(member));
        return chat.getId();
    }

    @Transactional
    public Conversation useRawChatBot(final String prompt, final Long chatId) {
        Chat chat = findChat(chatId);

        Words pureWords = Words.fromRawPrompt(prompt);
        Words duplicatedWords = Words.ofPureWords(wordRepository.findAllByWords(pureWords.getWordsToString()));
        duplicatedWords.updateFrequencyCount();

        List<Word> newWords = duplicatedWords.findNotContainsWordsFromOthers(pureWords.getWords());
        wordRepository.saveAll(newWords);

        String rawAnswer = chatBot.getRawAnswer(prompt);
        return conversationRepository.save(Conversation.from(prompt, rawAnswer, chat));
    }

    private Chat findChat(final Long chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new ChattingRoomNotFoundException(chatId));
    }

    @Transactional(readOnly = true)
    public ChattingHistoryResponse joinChattingRoom(final Long chatId) {
        Chat chat = findChat(chatId);

        List<Conversation> conversationsHistory = conversationRepository.findAllByChat(chat);
        return ChattingHistoryResponse.from(chat, conversationsHistory);
    }
}
