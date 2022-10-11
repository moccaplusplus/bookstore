package pl.szkolaspringa.bookstore.upload.application;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.szkolaspringa.bookstore.upload.application.port.StorageUseCase;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class StorageService implements StorageUseCase {

    @Value("${bookstore.uploads.target-dir}")
    private Path targetDir;

    @SneakyThrows
    @PostConstruct
    void init() {
        Files.createDirectories(targetDir);
    }

    @SneakyThrows
    @Override
    public void save(String filename, byte[] bytes) {
        Files.write(resolve(filename), bytes);
    }

    @SneakyThrows
    @Override
    public byte[] read(String filename) {
        return Files.readAllBytes(resolve(filename));
    }

    @SneakyThrows
    @Override
    public void delete(String filename) {
        var path = resolve(filename);
        if (Files.exists(path)) {
            Files.delete(path);
        }
    }

    @Override
    public Path resolve(String filename) {
        return targetDir.resolve(filename);
    }
}
