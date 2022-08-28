package pl.szkolaspringa.bookstore.upload.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.szkolaspringa.bookstore.upload.domain.Upload;

public interface UploadJpaRepository extends JpaRepository<Upload, Long> {
}
