package mju.chatuniv.board.infrasuructure.repository;

import java.util.List;
import mju.chatuniv.board.domain.Board;
import mju.chatuniv.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {

    boolean existsBoardById(Long boardId);

    List<Board> findAllByMemberOrderByIdDesc(Member member);
}
