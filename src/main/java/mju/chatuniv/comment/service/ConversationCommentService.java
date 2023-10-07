package mju.chatuniv.comment.service;

import mju.chatuniv.chat.domain.chat.Conversation;
import mju.chatuniv.chat.domain.chat.ConversationRepository;
import mju.chatuniv.chat.exception.exceptions.ConversationNotFoundException;
import mju.chatuniv.comment.domain.Comment;
import mju.chatuniv.comment.domain.CommentRepository;
import mju.chatuniv.comment.domain.ConversationComment;
import mju.chatuniv.comment.service.dto.CommentRequest;
import mju.chatuniv.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class ConversationCommentService implements CommentWriteService {

    private final ConversationRepository conversationRepository;
    private final CommentRepository commentRepository;

    public ConversationCommentService(ConversationRepository conversationRepository,
                                      CommentRepository commentRepository) {
        this.conversationRepository = conversationRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public Comment create(final Long conversationId, final Member member, final CommentRequest commentRequest) {
        Conversation conversation = findConversation(conversationId);
        ConversationComment conversationComment = ConversationComment.of(commentRequest.getContent(), member, conversation);
        commentRepository.save(conversationComment);
        return conversationComment;
    }

    private Conversation findConversation(final Long conversationId) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ConversationNotFoundException(conversationId));
    }
}
