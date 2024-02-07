package site.termterm.api.domain.term.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import static site.termterm.api.domain.term.dto.TermResponseDto.*;
import site.termterm.api.domain.term.entity.Term;

import java.util.List;

public interface TermRepository extends JpaRepository<Term, Long> {
    @Query("SELECT new site.termterm.api.domain.term.dto.TermResponseDto$TermIdAndNameAndBookmarkStatusResponseDto(t.id, t.name) " +
            "FROM Term t " +
            "WHERE t.name LIKE CONCAT('%', :name, '%') ")
    List<TermIdAndNameAndBookmarkStatusResponseDto> getSearchResults(@Param("name") String name);
}
