package site.termterm.api.domain.curation.domain.curation_paid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.termterm.api.domain.curation.domain.curation_paid.entity.CurationPaid;

public interface CurationPaidRepository extends JpaRepository<CurationPaid, Long> {

}
