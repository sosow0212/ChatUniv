package mju.chatuniv.member.domain;

import mju.chatuniv.member.exception.exceptions.MemberEmailFormatInvalidException;
import mju.chatuniv.member.exception.exceptions.MemberPasswordBlankException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;
import java.util.regex.Pattern;

@Entity
@Table(name = "MEMBER")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    protected Member() {
    }

    private Member(final Long id, final String email, final String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public static Member from(final String email, final String password) {
        validateCreateMember(email, password);
        return new Member(null, email, password);
    }

    public static Member from(final Long id, final String email, final String password) {
        validateCreateMember(email, password);
        return new Member(id, email, password);
    }

    private static void validateCreateMember(final String email, final String password) {
        if (!isEmailFormat(email)) {
            throw new MemberEmailFormatInvalidException(email);
        }

        if (isEmpty(password)) {
            throw new MemberPasswordBlankException();
        }
    }

    private static boolean isEmailFormat(final String email) {
        return Pattern.matches("^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$", email);
    }

    private static boolean isEmpty(final String password) {
        return password == null || password.isBlank();
    }

    public boolean isEmailSameWith(final String email) {
        return this.email.equals(email);
    }

    public boolean isPasswordSameWith(final String password) {
        return this.password.equals(password);
    }

    public void changePassword(final String newPassword) {
        this.password = newPassword;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Member)) return false;
        Member member = (Member) o;
        return Objects.equals(id, member.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
