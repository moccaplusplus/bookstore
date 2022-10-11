package pl.szkolaspringa.bookstore.upload.application.port;

import java.nio.file.Path;

public interface StorageUseCase {
    void save(String filename, byte[] bytes);

    byte[] read(String filename);

    void delete(String fileName);

    Path resolve(String filename);
}
