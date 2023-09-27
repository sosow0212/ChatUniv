package mju.chatuniv.member.service.service;

import java.util.List;
import java.util.stream.Collectors;
import mju.chatuniv.board.controller.dto.BoardResponse;
import mju.chatuniv.board.domain.Board;
import mju.chatuniv.board.domain.BoardRepository;
import mju.chatuniv.chat.domain.chat.Chat;
import mju.chatuniv.chat.domain.chat.ChatRepository;
import mju.chatuniv.comment.domain.CommentRepository;
import mju.chatuniv.comment.domain.dto.MembersCommentResponse;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.exception.exceptions.NewPasswordsNotMatchingException;
import mju.chatuniv.member.service.dto.ChangePasswordRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {
    private final ChatRepository chatRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    public MemberService(final ChatRepository chatRepository, final BoardRepository boardRepository, final CommentRepository commentRepository) {
        this.chatRepository = chatRepository;
        this.boardRepository = boardRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional(readOnly = true)
    public Member getUsingMemberIdAndEmail(final Member member) {
        return member;
    }

    @Transactional(readOnly = true)
    public List<Chat> findMembersChat(final Member member) {
        return chatRepository.findAllByMember(member);
    }

    @Transactional(readOnly = true)
    public List<BoardResponse> findMembersBoard(final Member member) {
        List<Board> boards = boardRepository.findAllByMemberOrderByIdDesc(member);

        return boards.stream()
                .map(BoardResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MembersCommentResponse> findMembersComment(final Member member) {
        return commentRepository.findMembersComment(member.getId());
    }

    @Transactional
    public Member changeMembersPassword(final Member member, final ChangePasswordRequest changePasswordRequest) {
        member.validatePassword(changePasswordRequest.getCurrentPassword());
        validateNewPassword(changePasswordRequest);
        member.changePassword(changePasswordRequest.getNewPassword());

        return member;
    }

    public void validateNewPassword(final ChangePasswordRequest changePasswordRequest) {
        String newPassword = changePasswordRequest.getNewPassword();
        String newPasswordCheck = changePasswordRequest.getNewPasswordCheck();

        if (!newPassword.equals(newPasswordCheck)) {
            throw new NewPasswordsNotMatchingException();
        }
    }
}
