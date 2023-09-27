package mju.chatuniv.board.domain;

import java.util.List;
import mju.chatuniv.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {

    boolean existsBoardById(Long boardId);

    List<Board> findAllByMemberOrderByIdDesc(Member member);
}
