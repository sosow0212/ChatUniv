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
public class BoardCommentService  {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    public BoardCommentService(final CommentRepository commentRepository,final BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public CommentResponse createBoardComment(final Long boardId, final Member member, final CommentCreateRequest commentCreateRequest) {
        Board board = boardRepository.findById(boardId).orElseThrow();
        BoardComment boardComment = new BoardComment(commentCreateRequest.getContent(), member, board);
        commentRepository.save(boardComment);

        return CommentResponse.from(boardComment);
    }

    @Transactional(readOnly = true)
    public CommentAllResponse findCommentsByBoard(final Long boardId, final Pageable pageable) {
        Board board = boardRepository.findById(boardId).orElseThrow();

        Page<BoardComment> commentPageInfo = commentRepository.findAllByBoard(pageable, board, boardId);
        PageInfo pageInfo = PageInfo.from(commentPageInfo);

        List<CommentResponse> comments = commentPageInfo.stream()
                .map(CommentResponse::from)
                .collect(collectingAndThen(toList(), Collections::unmodifiableList));

        return CommentAllResponse.from(comments, pageInfo);
    }
}

