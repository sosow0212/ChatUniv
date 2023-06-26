package mju.chatuniv.comment.domain;

import mju.chatuniv.board.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<BoardComment, Long> {

    Page<BoardComment> findAllByBoard(final Pageable pageable, final Board board);
}
