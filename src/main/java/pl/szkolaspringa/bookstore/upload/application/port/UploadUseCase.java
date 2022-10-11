package pl.szkolaspringa.bookstore.upload.application.port;

import pl.szkolaspringa.bookstore.upload.domain.Upload;

import java.util.Optional;

public interface UploadUseCase {
    Optional<Upload> getById(Long id);
}
