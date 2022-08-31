package pl.szkolaspringa.bookstore.order.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.szkolaspringa.bookstore.BaseEntity;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Recipient extends BaseEntity<Long> {

    private String name;

    private String phone;

    private String street;

    private String city;

    private String zipCode;

    private String email;
}
