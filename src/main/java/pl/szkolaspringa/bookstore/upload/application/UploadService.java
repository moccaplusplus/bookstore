package pl.szkolaspringa.bookstore.upload.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.szkolaspringa.bookstore.upload.application.port.StorageUseCase;
import pl.szkolaspringa.bookstore.upload.application.port.UploadUseCase;
import pl.szkolaspringa.bookstore.upload.db.UploadJpaRepository;
import pl.szkolaspringa.bookstore.upload.domain.Upload;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadService implements UploadUseCase {

    private final UploadJpaRepository uploadJpaRepository;

    private final StorageUseCase storageUseCase;

    @Transactional(readOnly = true)
    @Override
    public Optional<Upload> getById(Long id) {
        return uploadJpaRepository.findById(id);
    }
}
