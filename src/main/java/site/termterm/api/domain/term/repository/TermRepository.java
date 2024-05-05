package site.termterm.api.domain.term.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import site.termterm.api.domain.term.entity.Term;

public interface TermRepository extends JpaRepository<Term, Long>, TermRepositoryCustom {
}
