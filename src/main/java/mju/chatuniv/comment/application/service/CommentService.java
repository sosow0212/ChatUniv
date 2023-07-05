package mju.chatuniv.comment.application.service;

import mju.chatuniv.board.domain.Board;
import mju.chatuniv.board.domain.BoardRepository;
import mju.chatuniv.comment.application.dto.CommentAllResponse;
import mju.chatuniv.comment.application.dto.CommentCreateRequest;
import mju.chatuniv.comment.application.dto.CommentResponse;
import mju.chatuniv.comment.application.dto.PageInfo;
import mju.chatuniv.comment.domain.BoardComment;
import mju.chatuniv.comment.domain.CommentRepository;
import mju.chatuniv.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;


@Service
public class CommentService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    public CommentService(final BoardRepository boardRepository, final CommentRepository commentRepository) {
        this.boardRepository = boardRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public CommentResponse createBoardComment(final Long boardId, final Member member, final CommentCreateRequest commentCreateRequest, final Class<?> clazz) {
        Board board = boardRepository.findById(boardId).get();
        BoardComment boardComment = new BoardComment(commentCreateRequest.getContent(), member, board);
        commentRepository.save(boardComment);

        return CommentResponse.from(boardComment);
    }

    @Transactional(readOnly = true)
    public CommentAllResponse findCommentsByBoard(final Long boardId, final Pageable pageable) {
        Board board = boardRepository.findById(boardId).get();

        Page<BoardComment> commentPageInfo = commentRepository.findAllByBoard(pageable, board);
        PageInfo pageInfo = PageInfo.from(commentPageInfo);

        List<CommentResponse> comments = commentPageInfo.stream()
            .map(CommentResponse::from)
            .collect(collectingAndThen(toList(), Collections::unmodifiableList));

        return CommentAllResponse.from(comments, pageInfo);
    }

    @Transactional
    public CommentResponse updateComment(final Long commentId, final Member member, final CommentCreateRequest commentCreateRequest) {
        BoardComment comment = commentRepository.findById(commentId).get();

        comment.isWriter(member);
        comment.update(commentCreateRequest.getContent());

        return CommentResponse.from(comment);
    }

    @Transactional
    public void deleteComment(final Long commentId, final Member member) {
        BoardComment comment = commentRepository.findById(commentId).get();

        comment.isWriter(member);

        commentRepository.delete(comment);
    }
}
