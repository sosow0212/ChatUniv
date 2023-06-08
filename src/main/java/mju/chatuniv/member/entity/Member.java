package mju.chatuniv.member.entity;

import mju.chatuniv.member.exception.MemberEmailFormatInvalidException;
import mju.chatuniv.member.exception.MemberPasswordBlankException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.regex.Pattern;

@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    public Member() {
    }

    private Member(final Long id, final String email, final String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    private Member(final String email, final String password) {
        this.email = email;
        this.password = password;
    }

    public static Member from(final String email, final String password) {
        if (!validateEmailFormat(email)) {
            throw new MemberEmailFormatInvalidException(email);
        }

        if (password.isBlank()) {
            throw new MemberPasswordBlankException();
        }

        return new Member(email, password);
    }

    private static boolean validateEmailFormat(final String email) {
        return Pattern.matches("^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$", email);
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
}
