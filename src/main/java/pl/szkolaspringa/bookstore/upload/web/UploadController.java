package pl.szkolaspringa.bookstore.upload.web;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pl.szkolaspringa.bookstore.upload.application.port.UploadUseCase;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/uploads")
@RequiredArgsConstructor
public class UploadController {

    private final UploadUseCase uploadUseCase;

    @GetMapping("/{id}")
    public UploadInfoDto getUpload(@PathVariable Long id) {
        return uploadUseCase.getById(id)
                .map(file -> new UploadInfoDto(
                        file.getId(), file.getContentType(), file.getFileName(), file.getCreatedAt()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<?> getFile(@PathVariable Long id) {
        return uploadUseCase.getById(id)
                .map(file -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                        .contentType(MediaType.parseMediaType(file.getContentType()))
                        .body(new ByteArrayResource(file.getFile())))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public record UploadInfoDto(Long id, String contentType, String fileName, LocalDateTime createdAt) {
    }
}
