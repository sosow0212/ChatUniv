package mju.chatuniv.comment.domain;

import mju.chatuniv.board.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {


    @Query("select c from Comment c join BoardComment bc on c.id = bc.id where bc.board.id = :boardId")
    Page<BoardComment> findAllByBoard(final Pageable pageable, @Param("board") final Board board, @Param("boardId") Long boardId);

}
