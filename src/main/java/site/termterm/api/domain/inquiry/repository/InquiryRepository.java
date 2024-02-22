package site.termterm.api.domain.inquiry.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.termterm.api.domain.inquiry.entity.Inquiry;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    List<Inquiry> findByEmail(String email);
}
