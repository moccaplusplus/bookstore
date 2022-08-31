package pl.szkolaspringa.bookstore;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity<T> {

    @Id
    @GeneratedValue
    private Long id;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @Override
    public boolean equals(Object obj) {
        if (id == null) return obj == this;
        // we must take proxy classes into account!
        if (!getClass().isInstance(obj) && !obj.getClass().isInstance(this)) return false;
        return Objects.equals(id, ((BaseEntity<?>) obj).id);
    }

    @Override
    public int hashCode() {
        // must be fixed -> hibernate will update id (which is crucial for equality check) when saving set of entities.
        return 177745327;
    }
}
