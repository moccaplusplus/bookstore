package pl.szkolaspringa.bookstore.upload.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.szkolaspringa.bookstore.upload.application.port.UploadUseCase;
import pl.szkolaspringa.bookstore.upload.db.UploadJpaRepository;
import pl.szkolaspringa.bookstore.upload.domain.Upload;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UploadService implements UploadUseCase {

    private final UploadJpaRepository uploadJpaRepository;

    @Override
    public Upload save(SaveUploadCommand command) {
        var upload = new Upload(command.file(), command.contentType(), command.fileName());
        uploadJpaRepository.save(upload);
        System.out.println("Upload saved: " + upload.getFileName() + ", with id: " + upload.getId());
        return upload;
    }

    @Override
    public Optional<Upload> getById(Long id) {
        return uploadJpaRepository.findById(id);
    }

    @Override
    public void removeById(Long id) {
        uploadJpaRepository.deleteById(id);
    }
}
