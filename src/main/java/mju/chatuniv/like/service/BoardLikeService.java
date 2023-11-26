package mju.chatuniv.like.service;

import mju.chatuniv.board.domain.Board;
import mju.chatuniv.board.exception.exceptions.BoardNotFoundException;
import mju.chatuniv.board.infrasuructure.repository.BoardRepository;
import mju.chatuniv.like.domain.BoardLike;
import mju.chatuniv.like.domain.LikeRepository;
import mju.chatuniv.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class BoardLikeService {

    private final LikeRepository likeRepository;
    private final BoardRepository boardRepository;

    public BoardLikeService(final LikeRepository likeRepository, final BoardRepository boardRepository) {
        this.likeRepository = likeRepository;
        this.boardRepository = boardRepository;
    }

    public String change(final Long boardId, final Member member) {
        Board board = findBoard(boardId);
        BoardLike like = likeRepository.findLike(boardId, member);

        if (like != null) {
            delete(like, board);
            return "좋아요가 취소되었습니다";
        }
        create(member, board);
        return "좋아요를 추가했습니다";
    }

    private void delete(final BoardLike like, final Board board) {
        likeRepository.delete(like);
        board.likeCancel();
    }

    private void create(final Member member, final Board board) {
        BoardLike boardLike = BoardLike.of(member, board);
        likeRepository.save(boardLike);
        board.like();
    }

    private Board findBoard(final Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException(boardId));
    }
}
