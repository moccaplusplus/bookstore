package pl.szkolaspringa.bookstore.upload.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
public class Upload {
    @Id
    @GeneratedValue
    private Long id;
    private byte[] file;
    private String contentType;
    private String fileName;

    @CreatedDate
    private LocalDateTime createdAt;

    public Upload(byte[] file, String contentType, String fileName) {
        this.file = file;
        this.contentType = contentType;
        this.fileName = fileName;
    }
}
