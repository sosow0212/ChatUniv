package mju.chatuniv.board.domain;

import mju.chatuniv.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {

    boolean existsBoardById(Long boardId);

    List<Board> findAllByMember_IdOrderByIdDesc(Member member);
}
