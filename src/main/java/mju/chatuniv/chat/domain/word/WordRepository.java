package mju.chatuniv.chat.domain.word;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WordRepository extends JpaRepository<Word, Long> {

    @Query("SELECT w FROM Word w WHERE w.word IN :promptWords")
    List<Word> findAllByWords(@Param("promptWords") final List<String> promptWords);
}

