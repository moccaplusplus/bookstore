package pl.szkolaspringa.bookstore.upload.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.szkolaspringa.bookstore.BaseEntity;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Upload extends BaseEntity<Long> {

    private byte[] file;

    private String contentType;

    private String fileName;
}
