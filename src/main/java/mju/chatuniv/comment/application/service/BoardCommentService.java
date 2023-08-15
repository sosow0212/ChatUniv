package mju.chatuniv.comment.application.service;

import mju.chatuniv.board.domain.Board;
import mju.chatuniv.board.domain.BoardRepository;
import mju.chatuniv.board.exception.exceptions.BoardNotFoundException;
import mju.chatuniv.comment.application.dto.CommentRequest;
import mju.chatuniv.comment.domain.BoardComment;
import mju.chatuniv.comment.domain.Comment;
import mju.chatuniv.comment.domain.CommentRepository;
import mju.chatuniv.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BoardCommentService implements CommentService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    public BoardCommentService(final CommentRepository commentRepository, final BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    @Transactional
    public Comment create(final Long boardId, final Member member, final CommentRequest commentRequest) {
        Board board = findBoard(boardId);

        BoardComment boardComment = BoardComment.of(commentRequest.getContent(), member, board);
        commentRepository.save(boardComment);

        return boardComment;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Comment> findComments(final Long boardId, final Pageable pageable) {
        validateExistenceOfBoard(boardId);
        return commentRepository.findAllByBoardId(pageable, boardId);
    }

        private void validateExistenceOfBoard(final Long boardId) {
        if (!boardRepository.existsBoardById(boardId)) {
            throw new IllegalArgumentException();
        }
    }

    private Board findBoard(final Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException(boardId));
    }
}

