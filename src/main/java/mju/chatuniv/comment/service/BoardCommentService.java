package mju.chatuniv.comment.service;

import mju.chatuniv.board.domain.Board;
import mju.chatuniv.board.exception.exceptions.BoardNotFoundException;
import mju.chatuniv.board.infrasuructure.repository.BoardRepository;
import mju.chatuniv.comment.domain.BoardComment;
import mju.chatuniv.comment.domain.Comment;
import mju.chatuniv.comment.domain.CommentRepository;
import mju.chatuniv.comment.service.dto.CommentRequest;
import mju.chatuniv.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class BoardCommentService implements CommentWriteService{

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    public BoardCommentService(final CommentRepository commentRepository, final BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public Comment create(final Long boardId, final Member member, final CommentRequest commentRequest) {
        Board board = findBoard(boardId);
        BoardComment boardComment = BoardComment.of(commentRequest.getContent(), member, board);
        commentRepository.save(boardComment);
        return boardComment;
    }

    private Board findBoard(final Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException(boardId));
    }
}

