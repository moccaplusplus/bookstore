package pl.szkolaspringa.bookstore.upload.application.port;

import pl.szkolaspringa.bookstore.upload.domain.Upload;

import java.util.Optional;

public interface UploadUseCase {
    Upload save(SaveUploadCommand comamnd);

    Optional<Upload> getById(String id);

    void removeById(String coverId);

    record SaveUploadCommand(String fileName, byte[] file, String contentType) {
    }
}
