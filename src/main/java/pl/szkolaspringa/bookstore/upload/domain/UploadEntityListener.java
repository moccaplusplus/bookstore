package pl.szkolaspringa.bookstore.upload.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.szkolaspringa.bookstore.upload.application.port.StorageUseCase;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

@Component
@RequiredArgsConstructor
public class UploadEntityListener {

    private final StorageUseCase storageUseCase;

    @PostPersist
    public void onPostPersist(Upload upload) {
        storageUseCase.save(getStorageName(upload), upload.getFile());
    }

    @PostUpdate
    public void onPostUpdate(Upload upload) {
        storageUseCase.save(getStorageName(upload), upload.getFile());
    }

    @PostLoad
    public void onPostLoad(Upload upload) {
        upload.setFile(storageUseCase.read(getStorageName(upload)));
    }

    @PostRemove
    public void onPostRemove(Upload upload) {
        storageUseCase.delete(getStorageName(upload));
    }

    private String getStorageName(Upload upload) {
        return "upload_" + upload.getId();
    }
}
