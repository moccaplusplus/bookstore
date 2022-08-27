package pl.szkolaspringa.bookstore.upload.domain;

import java.time.LocalDateTime;

public record Upload(String id, byte[] file, String contentType, String fileName, LocalDateTime createdAt) {
}
