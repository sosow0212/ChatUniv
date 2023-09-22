package mju.chatuniv.chat.service;

import java.util.List;
import mju.chatuniv.chat.domain.chat.Chat;
import mju.chatuniv.chat.domain.chat.ChatRepository;
import mju.chatuniv.chat.domain.chat.Conversation;
import mju.chatuniv.chat.domain.chat.ConversationRepository;
import mju.chatuniv.chat.domain.word.Word;
import mju.chatuniv.chat.domain.word.WordRepository;
import mju.chatuniv.chat.domain.word.Words;
import mju.chatuniv.chat.exception.exceptions.ChattingRoomNotFoundException;
import mju.chatuniv.chat.exception.exceptions.OwnerInvalidException;
import mju.chatuniv.chat.infrastructure.ChatBot;
import mju.chatuniv.chat.service.dto.chat.ChattingHistoryResponse;
import mju.chatuniv.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Conversation useChatBot(final String prompt,
                                   final Long chatId,
                                   final boolean isMild,
                                   final Member member) {
        Chat chat = findChat(chatId);
        validateOwner(member, chat);

        Words pureWords = Words.fromRawPrompt(prompt);
        pureWords.updateStaticsCount();

        Words duplicatedWords = Words.ofPureWords(wordRepository.findAllByWords(pureWords.getWordsToString()));
        duplicatedWords.updateFrequencyCount();

        List<Word> newWords = duplicatedWords.findNotContainsWordsFromOthers(pureWords.getWords());
        wordRepository.saveAll(newWords);

        return getConversation(prompt, isMild, chat);
    }

    private void validateOwner(final Member member, final Chat chat) {
        if (!member.equals(chat.getId())) {
            throw new OwnerInvalidException();
        }
    }

    private Conversation getConversation(final String prompt, final boolean isMild, final Chat chat) {
        if (isMild) {
            String answer = chatBot.getMildAnswer(prompt);
            return conversationRepository.save(Conversation.from(prompt, answer, chat));
        }

        String answer = chatBot.getRawAnswer(prompt);
        return conversationRepository.save(Conversation.from(prompt, answer, chat));
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
