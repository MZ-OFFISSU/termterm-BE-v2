package site.termterm.api.domain.folder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.termterm.api.domain.folder.entity.Folder;

public interface FolderRepository extends JpaRepository<Folder, Long> {
}
