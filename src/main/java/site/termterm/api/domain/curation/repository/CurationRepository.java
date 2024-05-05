package site.termterm.api.domain.curation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.termterm.api.domain.curation.entity.Curation;
public interface CurationRepository extends JpaRepository<Curation, Long>, CurationRepositoryCustom {
}
