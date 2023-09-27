package mju.chatuniv.member.domain;

import mju.chatuniv.member.exception.exceptions.MemberUsernameInvalidException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "MEMBER")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 200)
    private String username;

    protected Member() {
    }

    private Member(final Long id, final String username) {
        this.id = id;
        this.username = username;
    }

    public static Member from(final String username) {
        validateCreateMember(username);
        return new Member(null, username);
    }

    private static void validateCreateMember(final String username) {
        if (username.isBlank()) {
            throw new MemberUsernameInvalidException();
        }
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Member)) {
            return false;
        }
        Member member = (Member) o;

        return Objects.equals(id, member.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
