package pl.szkolaspringa.bookstore.upload.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.szkolaspringa.bookstore.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(UploadEntityListener.class)
public class Upload extends BaseEntity<Long> {

    private transient byte[] file;

    private String contentType;

    private String filename;
}
