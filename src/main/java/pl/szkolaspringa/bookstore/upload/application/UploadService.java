package pl.szkolaspringa.bookstore.upload.application;

import org.springframework.stereotype.Service;
import pl.szkolaspringa.bookstore.upload.application.port.UploadUseCase;
import pl.szkolaspringa.bookstore.upload.domain.Upload;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UploadService implements UploadUseCase {

    private final Map<String, Upload> storage = new ConcurrentHashMap<>();

    @Override
    public Upload save(SaveUploadCommand command) {
        var nextId = UUID.randomUUID().toString();
        var upload = new Upload(nextId, command.file(), command.contentType(), command.fileName(), LocalDateTime.now());
        storage.put(upload.id(), upload);
        System.out.println("Upload saved: " + upload.fileName());
        return upload;
    }

    @Override
    public Optional<Upload> getById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public void removeById(String coverId) {
        storage.remove(coverId);
    }
}
