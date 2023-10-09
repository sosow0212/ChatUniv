package mju.chatuniv.comment.repository;

import static mju.chatuniv.fixture.board.BoardFixture.createBoard;
import static mju.chatuniv.fixture.comment.BoardCommentFixture.createBoardComment;
import static mju.chatuniv.fixture.member.MemberFixture.createMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import mju.chatuniv.board.domain.Board;
import mju.chatuniv.board.infrasuructure.repository.BoardRepository;
import mju.chatuniv.comment.domain.BoardComment;
import mju.chatuniv.comment.domain.Comment;
import mju.chatuniv.comment.domain.CommentRepository;
import mju.chatuniv.comment.infrastructure.repository.BoardCommentQueryRepository;
import mju.chatuniv.comment.infrastructure.repository.dto.CommentPagingResponse;
import mju.chatuniv.helper.RepositoryTestHelper;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@Import(BoardCommentQueryRepository.class)
class BoardCommentRepositoryTest extends RepositoryTestHelper {

    private Member member;
    private Board board;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BoardCommentQueryRepository boardCommentQueryRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BoardRepository boardRepository;

    @BeforeEach
    public void setUp() {
        member = createMember();
        board = createBoard(member);
        memberRepository.save(member);
        boardRepository.save(board);
    }

    @DisplayName("게시글의 댓글이 DB에 잘 저장되는지 확인한다.")
    @Test
    void save_comment() {
        //given
        BoardComment boardComment = createBoardComment(member, board);
        commentRepository.save(boardComment);

        //when
        Optional<Comment> comment = commentRepository.findById(1L);

        //then
        assertAll(
                () -> assertThat(comment).isPresent(),
                () -> assertThat(comment.get().getId()).isEqualTo(1L),
                () -> assertThat(comment.get().getMember()).isEqualTo(member),
                () -> assertThat(comment.get().getContent()).isEqualTo("content"));
    }

    @DisplayName("게시글의 댓글이 제대로 조회되는지 확인한다.")
    @Test
    void find_comment_by_id() {
        //given
        Member member = createMember();
        Board board = createBoard(member);
        BoardComment boardComment = createBoardComment(member, board);

        //when
        BoardComment saveComment = commentRepository.save(boardComment);

        //then
        assertThat(saveComment).usingRecursiveComparison().isEqualTo(boardComment);
    }

    @DisplayName("게시글의 id로 댓글이 제대로 조회되는지 확인한다.")
    @Test
    void find_comments_by_board_id() {
        //given
        IntStream.rangeClosed(1, 10)
                .forEach(index -> {
                    commentRepository.save(createBoardComment("content" + index, member, board));
                });

        //when
        List<CommentPagingResponse> comments = boardCommentQueryRepository.findComments(1L, 10, 6L);

        //then
        assertAll(
                () -> assertThat(comments).hasSize(5),
                () -> assertThat(comments.get(0).getCommentId()).isEqualTo(5L),
                () -> assertThat(comments.get(0).getContent()).isEqualTo("content5")
        );
    }
}
