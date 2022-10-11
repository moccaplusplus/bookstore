package pl.szkolaspringa.bookstore.upload.web;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.szkolaspringa.bookstore.upload.application.port.StorageUseCase;
import pl.szkolaspringa.bookstore.upload.application.port.UploadUseCase;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/uploads")
@RequiredArgsConstructor
public class UploadController {

    private final UploadUseCase uploadUseCase;

    private final StorageUseCase storageUseCase;

    @GetMapping("/{id}")
    public UploadInfoDto getUpload(@PathVariable Long id) {
        var upload = uploadUseCase.getById(id).orElseThrow();
        return UploadInfoDto.builder()
                .id(upload.getId())
                .contentType(upload.getContentType())
                .fileName(upload.getFilename())
                .createdAt(upload.getCreatedAt())
                .build();
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<Resource> getFile(@PathVariable Long id) {
        var upload = uploadUseCase.getById(id).orElseThrow();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + upload.getFilename() + "\"")
                .contentType(MediaType.parseMediaType(upload.getContentType()))
                .body(new ByteArrayResource(upload.getFile()));
    }

    @Builder
    public record UploadInfoDto(Long id, String contentType, String fileName, LocalDateTime createdAt) {
    }
}
